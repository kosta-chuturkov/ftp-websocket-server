package ftp.core.service.face;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface FileManagementService {

    String updateProfilePicture(HttpServletRequest request, MultipartFile file) throws IOException;

    String uploadFile(HttpServletRequest request,
                      MultipartFile file, String modifier,
                      String userNickNames) throws IOException;

    void deleteFiles(final HttpServletResponse response, @PathVariable final String deleteHash);

    void downloadFile(final HttpServletRequest request, final HttpServletResponse response);

    void getProfilePic(final HttpServletResponse response, @PathVariable String filename);

}
