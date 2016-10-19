package ftp.core.service.face;

import ftp.core.model.dto.ModifiedUserDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public interface UserManagementService {
    String getUserDetails(HttpServletRequest request, HttpServletResponse response,
                          String userNickName) throws IOException;

    void updateUsers(String deleteHash, Set<ModifiedUserDto> modifiedUserDto);

}
