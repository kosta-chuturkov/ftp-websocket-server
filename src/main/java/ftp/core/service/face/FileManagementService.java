package ftp.core.service.face;

import ftp.core.model.dto.DataTransferObject;
import ftp.core.model.dto.DeletedFilesDto;
import ftp.core.model.dto.JsonFileDto;
import ftp.core.model.dto.UploadedFilesDto;
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

    List<DataTransferObject> getFilesISharedWithOtherUsers(Integer firstResult, Integer maxResults);

    List<DataTransferObject> getPrivateFiles(Integer firstResult, Integer maxResults);

    List<DataTransferObject> getFilesSharedToMe(Integer firstResult, Integer maxResults);
}
