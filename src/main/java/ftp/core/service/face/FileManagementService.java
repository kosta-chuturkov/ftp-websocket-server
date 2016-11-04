package ftp.core.service.face;

import ftp.core.model.dto.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface FileManagementService {

    String updateProfilePicture(MultipartFile file);

    UploadedFilesDto<JsonFileDto> uploadFile(MultipartFile file,
                                             Set<String> userNickNames);

    DeletedFilesDto deleteFiles(String deleteHash);

    FileSystemResource downloadFile(String downloadHash);

    FileSystemResource sendProfilePicture(String filename);

    String getProfilePicUrl(final String userName, String serverContext);

    List<FileWithSharedUsersWithMeDto> getFilesISharedWithOtherUsers(Integer firstResult, Integer maxResults, String nickName);

    List<PrivateFileWithMeDto> getPrivateFiles(Integer firstResult, Integer maxResults, String nickName);

    List<SharedFileWithMeDto> getFilesSharedToMe(Integer firstResult, Integer maxResults, String nickName);
}
