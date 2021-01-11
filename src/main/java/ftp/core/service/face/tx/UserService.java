package ftp.core.service.face.tx;

import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.model.dto.RegistrationRequest;
import ftp.core.model.entities.File;
import ftp.core.model.entities.User;
import ftp.core.repository.projections.NickNameProjection;
import ftp.core.repository.projections.UploadedFilesProjection;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;

@Transactional
public interface UserService {

    User findByEmailAndPassword(String email, String password);

    Long getRandomTokenFromDB();

    User findUserByNickName(String nickName);

    User getUserByEmail(String email);

    void updateRemainingStorageForUser(long fileSize, String userId, long remainingStorage);

    List<NickNameProjection> getUserByNickLike(String userNickName);//

    void validateUserCredentials(String email, String password, String nickName,
                                 String password_repeated) throws IllegalArgumentException;

    User registerUser(RegistrationRequest registrationRequest)
            throws IllegalArgumentException;

    String getUserSaltedPassword(final String rawPassword, final Long token);//

    Set<NickNameProjection> findByNickNameIn(Collection<String> nickNames);//

    UploadedFilesProjection findUploadedFilesByUserId(Long userId);

    User save(User current);

    String getUserDetails();

    void updateUsers(String deleteHash, Set<ModifiedUserDto> modifiedUserDto);

    List<User> findAll();
}
