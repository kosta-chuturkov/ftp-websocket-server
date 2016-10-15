package ftp.core.service.face;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface FileManagementService {

    String updateProfilePicture(HttpServletRequest request, MultipartFile file) throws IOException;

    String uploadFile(HttpServletRequest request,
                      MultipartFile file, String modifier,
                      String userNickNames) throws IOException;
}
