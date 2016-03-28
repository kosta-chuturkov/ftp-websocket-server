package ftp.core.service.face.tx;

import ftp.core.common.model.User;
import ftp.core.persistance.face.generic.service.GenericService;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;

public interface UserService extends GenericService<User, Long> {

	User findByEmailAndPassword(String email, String password);

	BigDecimal getTokenByEmail(String email);

	BigDecimal getRandomTokenFromDB();

	User getUserByNickName(String nickName);

	User getUserByEmail(String email);

	void updateRemainingStorageForUser(long fileSize, Long userId, long remainingStorage);
	
	User checkAndGetUserToSendFilesTo(String userToSendFilesToNickName);
	
	List<String> getUserByNickLike(String userNickName);

	void validateUserCredentials(String email, String password, String nickName, String password_repeated,
			ModelAndView modelAndView) throws IllegalArgumentException;

	Long registerUser(String email, String password, String nickName, String password_repeated,
					  ModelAndView modelAndView) throws IllegalArgumentException;
}
