package ftp.core.service.face.tx;

import ftp.core.model.dto.FileSharedWithUsersDto;
import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.model.dto.PersonalFileDto;
import ftp.core.model.dto.SharedFileDto;
import ftp.core.model.entities.File;

import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;

import ftp.core.model.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Transactional
public interface FileService {

    File getFileByDownloadHash(String downloadHash);

    File findByDeleteHashAndCreatorNickName(String deleteHash, String creatorNickName);

    File saveFile(File fileToBeSaved, Set<String> userNickNames);

    boolean isUserFromFileSharedUsers(File fileId, String nickName);

    boolean isFileCreator(Long fileId, String userNickName);

    Page<FileSharedWithUsersDto> getFilesISharedWithOtherUsers(String userNickName, Pageable pageable);

    Page<PersonalFileDto> getPrivateFilesForUser(String userNickName, Pageable pageable);

    Page<SharedFileDto> getSharedFilesWithCurrent(String userNickName, Pageable pageable);

    void updateUsers(final String deleteHash, final Set<ModifiedUserDto> modifiedUserDto);

    void delete(Long id);

    File save(File file);

    Optional<File> findById(Long fileId);

    void shareFileWithUsers(File file, Set<String> sharedWithUsers);

    void shareFileWithUser(File file, User user);

    Set<String> getListOfUsersFileIsSharedWith(File findByDeleteHash);

    Page<File> findAllFiles(Pageable pageable, String fileType);

    Page<File> findByQuery(String query, String fileType, Pageable pageable);
}
