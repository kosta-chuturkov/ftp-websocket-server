package ftp.core.service.face.tx;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.servlet.ModelAndView;

import ftp.core.common.model.AbstractEntity;
import ftp.core.common.model.User;
import ftp.core.persistance.face.generic.service.GenericService;

public interface UserService extends GenericService<AbstractEntity, Number> {

	User findByEmailAndPassword(String email, String password);

	BigDecimal getTokenByEmail(String email);

	BigDecimal getRandomTokenFromDB();

	User getUserByNickName(String nickName);

	User getUserByEmail(String email);
	
	void updateRemainingStorageForUser(long fileSize, Number userId, long remainingStorage);
	
	User checkAndGetUserToSendFilesTo(String userToSendFilesToNickName);
	
	List<String> getUserByNickLike(String userNickName);

	void validateUserCredentials(String email, String password, String nickName, String password_repeated,
			ModelAndView modelAndView) throws IllegalArgumentException;

	Number registerUser(String email, String password, String nickName, String password_repeated,
			ModelAndView modelAndView)throws IllegalArgumentException;
}
