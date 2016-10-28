package ftp.core.service.face;

import ftp.core.model.dto.ModifiedUserDto;

import java.io.IOException;
import java.util.Set;

public interface UserManagementService {
    String getUserDetails(String userNickName) throws IOException;

    void updateUsers(String deleteHash, Set<ModifiedUserDto> modifiedUserDto);

}
