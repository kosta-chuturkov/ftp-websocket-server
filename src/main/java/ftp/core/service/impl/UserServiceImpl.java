package ftp.core.service.impl;

import ftp.core.common.model.User;
import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;
import ftp.core.persistance.face.dao.UserDao;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import ftp.core.service.generic.AbstractGenericService;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service("userService")
public class UserServiceImpl extends AbstractGenericService<User, Long> implements UserService {

	@Resource
	private UserDao userDao;

	@Override
	public User findByEmailAndPassword(final String email, final String password) {
		return this.userDao.findByEmailAndPassword(email, password);
	}

	@Override
	public BigDecimal getTokenByEmail(final String email) {
		return this.userDao.getTokenByEmail(email);
	}

	@Override
	public BigDecimal getRandomTokenFromDB() {
		return this.userDao.getRandomTokenFromDB();
	}

	@Override
	public User getUserByNickName(final String nickName) {
		return this.userDao.getUserByNickName(nickName);
	}

	@Override
	public User getUserByEmail(final String email) {
		return this.userDao.getUserByEmail(email);
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
		return this.userDao.getUserByNickLike(userNickName);
	}

	@Override
	public Long registerUser(final String email, final String password, final String nickName, final String password_repeated,
							 final ModelAndView modelAndView) throws IllegalArgumentException {
		validateUserCredentials(email, password, nickName, password_repeated, modelAndView);
		final BigDecimal randomTokenFromDB = getRandomTokenFromDB();
		final String encodedPassword = ServerUtil.digestRawPassword(password, ServerUtil.SALT,
				randomTokenFromDB.toPlainString());
		final User user = new User(nickName, email, encodedPassword, ServerConstants.USER_MAX_UPLOAD_IN_BYTES,
				randomTokenFromDB);
		return save(user);
	}

	@Override
	public void validateUserCredentials(final String email, final String password, final String nickName, final String password_repeated,
										final ModelAndView modelAndView) throws IllegalArgumentException {
		if (!ServerUtil.isEmailValid(email)) {
			throw new IllegalArgumentException("Wrong email format");
		}
		final User userByEmail = getUserByEmail(email);
		if (userByEmail != null) {
			throw new IllegalArgumentException("User with this email already exists.");
		}

		if (!ServerUtil.isNickNameValid(nickName)) {
			throw new IllegalArgumentException("Wrong nickname format.");
		}
		final User userByNickName = getUserByNickName(nickName);
		if (userByNickName != null) {
			throw new IllegalArgumentException("User with this nickname already exists.");
		}

		if (!ServerUtil.isPasswordValid(password)) {
			throw new IllegalArgumentException("Wrong password format.");
		}
		if (!password.equals(password_repeated)) {
			throw new IllegalArgumentException("Passwords do not match.");
		}
	}

}
