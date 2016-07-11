package ftp.core.service.face.tx;

import java.util.List;

import org.springframework.web.servlet.ModelAndView;

import ftp.core.common.model.File;
import ftp.core.common.model.User;
import ftp.core.persistance.face.generic.service.GenericService;

public interface UserService extends GenericService<User, Long> {

    User findByEmailAndPassword(String email, String password);

    Long getTokenByEmail(String email);

    Long getRandomTokenFromDB();

    User getUserByNickName(String nickName);

    User getUserByEmail(String email);

    void updateRemainingStorageForUser(long fileSize, Long userId, long remainingStorage);

    User checkAndGetUserToSendFilesTo(String userToSendFilesToNickName);

    List<String> getUserByNickLike(String userNickName);

    void validateUserCredentials(String email, String password, String nickName, String password_repeated,
                                 ModelAndView modelAndView) throws IllegalArgumentException;

    Long registerUser(String email, String password, String nickName, String password_repeated,
                      ModelAndView modelAndView) throws IllegalArgumentException;

    File addFileToUser(final Long fileId, final Long userId);

	String getUserSaltedPassword(final String rawPassword, final Long token);
}
