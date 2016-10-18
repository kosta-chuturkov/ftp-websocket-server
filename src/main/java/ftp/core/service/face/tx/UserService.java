package ftp.core.service.face.tx;

import ftp.core.model.entities.File;
import ftp.core.model.entities.User;
import ftp.core.service.generic.GenericService;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface UserService extends GenericService<User, Long> {

    User findByEmailAndPassword(String email, String password);

    Long getTokenByEmail(String email);

    Long getRandomTokenFromDB();

    User getUserByNickName(String nickName);

    User getUserByEmail(String email);

    void updateRemainingStorageForUser(long fileSize, Long userId, long remainingStorage);

    User checkAndGetUserToSendFilesTo(String userToSendFilesToNickName);

    List<String> getUserByNickLike(String userNickName);

    void validateUserCredentials(String email, String password, String nickName, String password_repeated) throws IllegalArgumentException;

    User registerUser(String email, String password, String nickName, String password_repeated) throws IllegalArgumentException;

    File addFileToUser(final Long fileId, final Long userId);

    String getUserSaltedPassword(final String rawPassword, final Long token);
}
