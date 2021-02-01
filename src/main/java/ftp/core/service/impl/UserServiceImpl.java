package ftp.core.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ftp.core.constants.ServerConstants;
import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.model.dto.RegistrationRequest;
import ftp.core.model.entities.Authority;
import ftp.core.model.entities.User;
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

import ftp.core.util.ServerUtil;
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
    public UserServiceImpl(UserRepository userRepository, @Lazy FileService fileService,
                           PasswordEncoder passwordEncoder, AuthorityService authorityService) {
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.passwordEncoder = passwordEncoder;
        this.authorityService = authorityService;
    }

    @Secured(Authorities.USER)
    public String getUserDetails() {
        final JsonObject jsonResponse = new JsonObject();
        final JsonArray jsonArrayWrapper = new JsonArray();
        List<User> userByNickLike = userRepository.findAll();
        userByNickLike
                .forEach(user -> {
                    if (!User.getCurrent().getId().equals(user.getId())) {
                        final JsonObject userObject = new JsonObject();
                        userObject.addProperty("value", user.getNickName());
                        userObject.addProperty("display", user.getNickName());
                        jsonArrayWrapper.add(userObject);
                    }
                });

        jsonResponse.addProperty("total_count", userByNickLike.size());
        jsonResponse.add("items", jsonArrayWrapper);

        return jsonArrayWrapper.toString();
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
    public User findById(Long userId) {
        return this.userRepository.findById(userId).orElse(null);
    }

    @Override
    public String getUserSaltedPassword(final String rawPassword, final Long token) {
        String initialHash = ServerUtil.hashSHA256(SALT + rawPassword);
        return ServerUtil.hashSHA256(initialHash + token.toString());
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
    public User findUserByNickName(final String nickName) {
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
    public User registerUser(RegistrationRequest registrationRequest) throws IllegalArgumentException {
        String email = registrationRequest.getEmail();
        String nickName = registrationRequest.getNickname();
        String password = registrationRequest.getPassword();
        String password_repeated = registrationRequest.getPasswordRepeated();
        validateUserCredentials(email, password, nickName, password_repeated);
        final Long randomTokenFromDB = getRandomTokenFromDB();
        final String saltedPassword = getUserSaltedPassword(password, randomTokenFromDB);
        final User user = new User.Builder()
                .withNickName(nickName)
                .withEmail(email)
                .withPassword(saltedPassword)
                .withRemainingStorage(ServerConstants.USER_MAX_UPLOAD_IN_BYTES)
                .withToken(randomTokenFromDB)
                .withAccountNonExpired(true)
                .withAccountNonLocked(true)
                .withCredentialsNonExpired(true)
                .withEnabled(true)
                .build();

        User savedUser = save(user);
        return addAuthority(savedUser);
    }

    private User addAuthority(User savedUser) {
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
        if (!isNickNameValid(nickName)) {
            throw new UsernameNotFoundException("Nickname should be at least 3 symbols, only letters, numbers and '.' or '_'");
        }
        if (!isPasswordValid(password)) {
            throw new UsernameNotFoundException("Password should be between 6 and 64 characters");
        }
        if (!password.equals(password_repeated)) {
            throw new UsernameNotFoundException("Provided password and password repeated dont match");
        }

        final User userByEmail = getUserByEmail(email);
        if (userByEmail != null) {
            throw new UsernameNotFoundException("User with this email already exists.");
        }

        final User userByNickName = findUserByNickName(nickName);
        if (userByNickName != null) {
            throw new UsernameNotFoundException("User with this nickname already exists.");
        }


    }

    public boolean isPasswordValid(final String password) {
        return (password != null
                && password.length() >= ServerConstants.MINIMUM_PASSWORD_LENGTH
                && password.length() <= ServerConstants.MAXIMUM_PASSWORD_lENGTH);

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
