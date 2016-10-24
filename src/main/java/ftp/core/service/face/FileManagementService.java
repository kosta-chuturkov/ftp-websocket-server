package ftp.core.service.face;

import ftp.core.model.dto.DataTransferObject;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface FileManagementService {

    String updateProfilePicture(HttpServletRequest request, MultipartFile file) throws IOException;

    String uploadFile(HttpServletRequest request,
                      MultipartFile file,
                      String userNickNames) throws IOException;

    void deleteFiles(HttpServletResponse response, String deleteHash);

    void downloadFile(String downloadHash, HttpServletResponse response);

    void sendProfilePicture(HttpServletResponse response, String filename);

    void sendResourceByName(HttpServletResponse response, org.springframework.core.io.Resource resource);

    String getProfilePicUrl(final String userName, String serverContext);

    List<DataTransferObject> getFilesISharedWithOtherUsers(Integer firstResult, Integer maxResults);

    List<DataTransferObject> getPrivateFiles(Integer firstResult, Integer maxResults);

    List<DataTransferObject> getFilesSharedToMe(Integer firstResult, Integer maxResults);
}
