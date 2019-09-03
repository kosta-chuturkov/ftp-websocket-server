package ftp.core.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ftp.core.constants.ServerConstants;
import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.model.entities.Authority;
import ftp.core.model.entities.File;
import ftp.core.model.entities.User;
import ftp.core.repository.FileRepository;
import ftp.core.repository.UserRepository;
import ftp.core.repository.projections.NickNameProjection;
import ftp.core.repository.projections.UploadedFilesProjection;
import ftp.core.security.Authorities;
import ftp.core.service.face.tx.AuthorityService;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.UserService;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

  static final String SALT = "fKWCH(1UafNFK&QK-Vg`FEG(sAE5f^Q.vEA-+Wj?]Sbc+<crP,x]7M/+S}dnb-,^";

  private UserRepository userRepository;

  private FileService fileService;

  private PasswordEncoder passwordEncoder;

  private AuthorityService authorityService;

  private SecureRandom random = new SecureRandom();

  @Autowired
  public UserServiceImpl(UserRepository userRepository,@Lazy FileService fileService,
      PasswordEncoder passwordEncoder, AuthorityService authorityService) {
    this.userRepository = userRepository;
    this.fileService = fileService;
    this.passwordEncoder = passwordEncoder;
    this.authorityService = authorityService;
  }

  @Secured(Authorities.USER)
  public String getUserDetails(final String userNickName) {
    final JsonObject jsonResponse = new JsonObject();
    final JsonArray jsonArrayWrapper = new JsonArray();
    List<NickNameProjection> userByNickLike = getUserByNickLike(userNickName);
    userByNickLike
        .forEach(userName -> {
          final JsonObject userObject = new JsonObject();
          userObject.addProperty("id", userName.getNickName());
          userObject.addProperty("full_name", userName.getNickName());
          final JsonObject owner = new JsonObject();
          owner.addProperty("id", Math.random());
          userObject.add("owner", owner);
          jsonArrayWrapper.add(userObject);
        });

    jsonResponse.addProperty("total_count", userByNickLike.size());
    jsonResponse.addProperty("incomplete_results", Boolean.FALSE);
    jsonResponse.add("items", jsonArrayWrapper);

    return jsonResponse.toString();
  }


  @Secured(Authorities.USER)
  public void updateUsers(final String deleteHash, final Set<ModifiedUserDto> modifiedUserDto) {
    this.fileService.updateUsers(deleteHash, modifiedUserDto);
  }

  @Override
  public List<User> findAll() {
    return (List<User>) userRepository.findAll();
  }

  @Override
  public String getUserSaltedPassword(final String rawPassword, final Long token) {
    return SALT + rawPassword + token.toString();
  }

  @Override
  public Set<NickNameProjection> findByNickNameIn(Collection<String> nickNames) {
    return this.userRepository.findByNickNameIn(nickNames);
  }

  @Override
  public UploadedFilesProjection findUploadedFilesByUserId(Long userId) {
    return this.userRepository.findUploadedFilesById(userId);
  }

  @Override
  public User save(User current) {
    return userRepository.save(current);
  }

  public String encodePassword(final String rawPassword) {
    return this.passwordEncoder.encode(rawPassword);
  }

  @Override
  public User findByEmailAndPassword(final String email, final String password) {
    return this.userRepository.findByEmailAndPassword(email, password);
  }

  @Override
  public Long getRandomTokenFromDB() {
    return Math.round(this.random.nextDouble() * 1000000);
  }

  @Override
  public User getUserByNickName(final String nickName) {
    return this.userRepository.findByNickName(nickName);
  }

  @Override
  public User getUserByEmail(final String email) {
    return this.userRepository.findByEmail(email);
  }

  public void updateRemainingStorageForUser(final long fileSize, final String email,
      long remainingStorage) {
    remainingStorage -= fileSize;
    final User userById = getUserByEmail(email);
    userById.setRemainingStorage(remainingStorage);
    save(userById);
  }

  @Override
  public List<NickNameProjection> getUserByNickLike(final String userNickName) {
    String escapedUserName = StringEscapeUtils.escapeSql(userNickName);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("%").append(escapedUserName).append("%");
    return this.userRepository.findByNickNameLike(stringBuilder.toString());
  }

  @Override
  public User registerUser(final String email, final String nickName, final String password,
      final String password_repeated) throws IllegalArgumentException {
    validateUserCredentials(email, password, nickName, password_repeated);
    final Long randomTokenFromDB = getRandomTokenFromDB();
    final String saltedPassword = getUserSaltedPassword(password, randomTokenFromDB);
    final String hashedPassword = encodePassword(saltedPassword);
    final User user = new User.Builder()
        .withNickName(nickName)
        .withEmail(email)
        .withPassword(hashedPassword)
        .withRemainingStorage(ServerConstants.USER_MAX_UPLOAD_IN_BYTES)
        .withToken(randomTokenFromDB)
        .withAccountNonExpired(true)
        .withAccountNonLocked(true)
        .withCredentialsNonExpired(true)
        .withEnabled(true)
        .build();

    User savedUser = save(user);
    Optional<User> registeredUserOpt = userRepository.findById(savedUser.getId());
    if (registeredUserOpt.isPresent()) {
      User registeredUser = registeredUserOpt.get();
      Authority authority = new Authority(Authorities.USER);
      this.authorityService.save(authority);
      registeredUser.addAuthority(authority);
      save(registeredUser);
      return registeredUser;
    }
    return null;
  }

  @Override
  public void validateUserCredentials(final String email, final String password,
      final String nickName, final String password_repeated) throws UsernameNotFoundException {
    if (!isEmailValid(email)) {
      throw new UsernameNotFoundException("Wrong email format");
    }
    final User userByEmail = getUserByEmail(email);
    if (userByEmail != null) {
      throw new UsernameNotFoundException("User with this email already exists.");
    }

    if (!isNickNameValid(nickName)) {
      throw new UsernameNotFoundException("Wrong nickname format.");
    }
    final User userByNickName = getUserByNickName(nickName);
    if (userByNickName != null) {
      throw new UsernameNotFoundException("User with this nickname already exists.");
    }

    if (!isPasswordValid(password)) {
      throw new UsernameNotFoundException("Wrong password format.");
    }
    if (!password.equals(password_repeated)) {
      throw new UsernameNotFoundException("Passwords do not match.");
    }
  }

  public boolean isPasswordValid(final String password) {
    if (password == null) {
      return false;
    }
    if (password.length() < ServerConstants.MINIMUM_PASSWORD_LENGTH) {
      return false;
    }
    if (password.length() > ServerConstants.MAXIMUM_PASSWORD_lENGTH) {
      return false;
    }
    return true;
  }

  public boolean isEmailValid(final String email) {
    if (email == null || email.length() < 3 || email.length() > 30) {
      return false;
    }
    return org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(email);
  }

  public boolean isNickNameValid(final String username) {
    if (username != null && username.length() > 3 && username.length() < 30) {
      return username.matches(ServerConstants.USER_REGEX);
    }
    return false;
  }
}
