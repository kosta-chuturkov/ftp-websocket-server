package ftp.core.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import ftp.core.common.model.AbstractEntity;
import ftp.core.common.model.File;
import ftp.core.common.model.User;
import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;
import ftp.core.persistance.face.dao.UserDao;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import ftp.core.service.generic.AbstractGenericService;

@Service("userService")
public class UserServiceImpl extends AbstractGenericService<User, Long> implements UserService {

	@Resource
	private UserDao userDao;

	@Override
	public User findByEmailAndPassword(String email, String password) {
		return userDao.findByEmailAndPassword(email, password);
	}

	@Override
	public BigDecimal getTokenByEmail(String email) {
		return userDao.getTokenByEmail(email);
	}

	@Override
	public BigDecimal getRandomTokenFromDB() {
		return userDao.getRandomTokenFromDB();
	}

	@Override
	public User getUserByNickName(String nickName) {
		return userDao.getUserByNickName(nickName);
	}

	@Override
	public User getUserByEmail(String email) {
		return userDao.getUserByEmail(email);
	}

	public void updateRemainingStorageForUser(long fileSize, Number userId, long remainingStorage) {
		remainingStorage -= fileSize;
		User userById = (User) findOne(userId);
		userById.setRemainingStorage(remainingStorage);
		update(userById);
	}

	public User checkAndGetUserToSendFilesTo(String userToSendFilesToNickName) {
		String escapedUserName = StringEscapeUtils.escapeSql(userToSendFilesToNickName);
		if (!ServerUtil.isNickNameValid(escapedUserName)) {
			throw new FtpServerException("Provided user to share files to is invalid!");
		}
		if (User.getCurrent().getNickName().equals(escapedUserName)) {
			throw new FtpServerException("You cant share files with yourself!");
		}
		User userByNickName = getUserByNickName(escapedUserName);
		if (userByNickName == null) {
			throw new FtpServerException(
					"Unable to share file with user:" + escapedUserName + ".This user does not exist!");
		}
		return userByNickName;
	}

	@Override
	public List<String> getUserByNickLike(String userNickName) {
		return userDao.getUserByNickLike(userNickName);
	}

	@Override
	public Number registerUser(String email, String password, String nickName, String password_repeated,
			ModelAndView modelAndView) throws IllegalArgumentException {
		validateUserCredentials(email, password, nickName, password_repeated, modelAndView);
		BigDecimal randomTokenFromDB = getRandomTokenFromDB();
		String encodedPassword = ServerUtil.digestRawPassword(password, ServerUtil.SALT,
				randomTokenFromDB.toPlainString());
		User user = new User(nickName, email, encodedPassword, ServerConstants.USER_MAX_UPLOAD_IN_BYTES,
				randomTokenFromDB);
		return save(user);
	}

	@Override
	public void validateUserCredentials(String email, String password, String nickName, String password_repeated,
			ModelAndView modelAndView) throws IllegalArgumentException{
		if (!ServerUtil.isEmailValid(email)) {
			throw new IllegalArgumentException("Wrong email format");
		}
		User userByEmail = getUserByEmail(email);
		if (userByEmail != null) {
			throw new IllegalArgumentException("User with this email already exists.");
		}

		if (!ServerUtil.isNickNameValid(nickName)) {
			throw new IllegalArgumentException("Wrong nickname format.");
		}
		User userByNickName = getUserByNickName(nickName);
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
