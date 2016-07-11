package ftp.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import ftp.core.common.model.File;
import ftp.core.common.model.User;
import ftp.core.common.util.ServerUtil;
import ftp.core.constants.ServerConstants;
import ftp.core.persistance.face.repository.FileRepository;
import ftp.core.persistance.face.repository.UserRepository;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import ftp.core.service.generic.AbstractGenericService;

@Service("userService")
public class UserServiceImpl extends AbstractGenericService<User, Long> implements UserService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private FileRepository fileRepository;

	@Resource
	private PasswordEncoder passwordEncoder;

	@Override
	public String getUserSaltedPassword(final String rawPassword, final Long token) {
		return ServerUtil.SALT + rawPassword + token.toString();
	}

	public String encodePassword(final String rawPassword) {
		return this.passwordEncoder.encode(rawPassword);
	}

    @Override
    public File addFileToUser(final Long fileId, final Long userId) {
        final File file = this.fileRepository.findOne(fileId);
        if (file != null) {
            final User user = findOne(userId);
            user.addUploadedFile(file);
            update(user);
        }
        return file;
    }

    @Override
    public User findByEmailAndPassword(final String email, final String password) {
        return this.userRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public Long getTokenByEmail(final String email) {
        return this.userRepository.getTokenByEmail(email);
    }

    @Override
    public Long getRandomTokenFromDB() {
        return this.userRepository.getRandomTokenFromDB();
    }

    @Override
    public User getUserByNickName(final String nickName) {
        return this.userRepository.getUserByNickName(nickName);
    }

    @Override
    public User getUserByEmail(final String email) {
        return this.userRepository.getUserByEmail(email);
    }

    public void updateRemainingStorageForUser(final long fileSize, final Long userId, long remainingStorage) {
        remainingStorage -= fileSize;
        final User userById = (User) findOne(userId);
        userById.setRemainingStorage(remainingStorage);
        update(userById);
    }

    public User checkAndGetUserToSendFilesTo(final String userToSendFilesToNickName) {
        final String escapedUserName = StringEscapeUtils.escapeSql(userToSendFilesToNickName);
        if (!ServerUtil.isNickNameValid(escapedUserName)) {
            throw new FtpServerException("Provided user to share files to is invalid!");
        }
        if (User.getCurrent().getNickName().equals(escapedUserName)) {
            throw new FtpServerException("You cant share files with yourself!");
        }
        final User userByNickName = getUserByNickName(escapedUserName);
        if (userByNickName == null) {
            throw new FtpServerException(
                    "Unable to share file with user:" + escapedUserName + ".This user does not exist!");
        }
        return userByNickName;
    }

    @Override
    public List<String> getUserByNickLike(final String userNickName) {
        return this.userRepository.getUserByNickLike(userNickName);
    }

    @Override
    public Long registerUser(final String email, final String password, final String nickName, final String password_repeated,
                             final ModelAndView modelAndView) throws IllegalArgumentException {
        validateUserCredentials(email, password, nickName, password_repeated, modelAndView);
        final Long randomTokenFromDB = getRandomTokenFromDB();
		final String saltedPassword = getUserSaltedPassword(password, randomTokenFromDB);
		final String hashedPassword = encodePassword(saltedPassword);
		final User user = new User(nickName, email, hashedPassword, ServerConstants.USER_MAX_UPLOAD_IN_BYTES,
                randomTokenFromDB);
		return save(user);
    }

    @Override
    public void validateUserCredentials(final String email, final String password, final String nickName, final String password_repeated,
 final ModelAndView modelAndView) throws UsernameNotFoundException {
        if (!ServerUtil.isEmailValid(email)) {
			throw new UsernameNotFoundException("Wrong email format");
        }
        final User userByEmail = getUserByEmail(email);
        if (userByEmail != null) {
			throw new UsernameNotFoundException("User with this email already exists.");
        }

        if (!ServerUtil.isNickNameValid(nickName)) {
			throw new UsernameNotFoundException("Wrong nickname format.");
        }
        final User userByNickName = getUserByNickName(nickName);
        if (userByNickName != null) {
			throw new UsernameNotFoundException("User with this nickname already exists.");
        }

        if (!ServerUtil.isPasswordValid(password)) {
			throw new UsernameNotFoundException("Wrong password format.");
        }
        if (!password.equals(password_repeated)) {
			throw new UsernameNotFoundException("Passwords do not match.");
        }
    }
}
