package ftp.core.service.face.tx;

import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.model.entities.File;
import ftp.core.service.generic.GenericService;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Transactional
public interface FileService extends GenericService<File, Long> {
    File getFileByDownloadHash(String downloadHash);

    File findByDeleteHash(String deleteHash, String creatorNickName);

    void createFileRecord(String fileNameEscaped, long timestamp,Set<String> users, long fileSize,
                          String deleteHash, String downloadHash);

    boolean isUserFromFileSharedUsers(Long fileId, String nickName);

    boolean isFileCreator(Long fileId, String userNickName);

    List<File> getFilesISharedWithOtherUsers(String userNickName, int firstResult, int maxResults);

    List<File> getPrivateFilesForUser(String userNickName, int firstResult, int maxResults);

    List<File> getSharedFilesWithMe(String userNickName, int firstResult, int maxResults);

    File updateUsersForFile(final String fileHash, final Set<String> userNickNames);

    void updateUsers(final String deleteHash, final Set<ModifiedUserDto> modifiedUserDto);

    List<File> findByCreatorId(Long creatorId, Pageable pageable);
}
