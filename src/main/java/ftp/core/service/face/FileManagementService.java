package ftp.core.service.face;

import ftp.core.model.dto.DataTransferObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileManagementService {

    String updateProfilePicture(MultipartFile file);

    String uploadFile(MultipartFile file,
                      String userNickNames);

    String deleteFiles(String deleteHash);

    FileSystemResource downloadFile(String downloadHash);

    FileSystemResource sendProfilePicture(String filename);

    String getProfilePicUrl(final String userName, String serverContext);

    List<DataTransferObject> getFilesISharedWithOtherUsers(Integer firstResult, Integer maxResults);

    List<DataTransferObject> getPrivateFiles(Integer firstResult, Integer maxResults);

    List<DataTransferObject> getFilesSharedToMe(Integer firstResult, Integer maxResults);
}
