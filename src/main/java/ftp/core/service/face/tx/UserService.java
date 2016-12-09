package ftp.core.service.face.tx;

import ftp.core.model.entities.File;
import ftp.core.model.entities.User;
import ftp.core.repository.projections.NickNameProjection;
import ftp.core.repository.projections.UploadedFilesProjection;
import ftp.core.service.generic.GenericService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserService extends GenericService<User, String> {

    User findByEmailAndPassword(String email, String password);

    Long getRandomTokenFromDB();

    User getUserByNickName(String nickName);

    User getUserByEmail(String email);

    void updateRemainingStorageForUser(long fileSize, String userId, long remainingStorage);

    List<NickNameProjection> getUserByNickLike(String userNickName);//

    void validateUserCredentials(String email, String password, String nickName, String password_repeated) throws IllegalArgumentException;

    User registerUser(String email, String nickName, String password, String password_repeated) throws IllegalArgumentException;

    File addFileToUser(final String fileId, final String userId);//

    String getUserSaltedPassword(final String rawPassword, final Long token);//

    Set<NickNameProjection> findByNickNameIn(Collection<String> nickNames);//

    UploadedFilesProjection findUploadedFilesByUserId(String userId);
}
