package ftp.core.service.face.tx;

import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.model.entities.File;
import ftp.core.service.generic.GenericService;

import java.util.List;
import java.util.Set;

public interface FileService extends GenericService<File, String> {
    File getFileByDownloadHash(String downloadHash);

    File findByDeleteHash(String deleteHash, String creatorNickName);

    void saveFile(File fileToBeSaved);

    boolean isUserFromFileSharedUsers(String fileId, String nickName);

    boolean isFileCreator(String fileId, String userNickName);

    List<File> getFilesISharedWithOtherUsers(String userNickName, int firstResult, int maxResults);

    List<File> getPrivateFilesForUser(String userNickName, int firstResult, int maxResults);

    List<File> getSharedFilesWithMe(String userNickName, int firstResult, int maxResults);

    void updateUsers(final String deleteHash, final Set<ModifiedUserDto> modifiedUserDto);
}
