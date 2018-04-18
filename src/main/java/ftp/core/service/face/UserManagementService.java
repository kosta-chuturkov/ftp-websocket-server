package ftp.core.service.face;

import ftp.core.model.dto.ModifiedUserDto;
import java.util.Set;

public interface UserManagementService {

  String getUserDetails(String userNickName);

  void updateUsers(String deleteHash, Set<ModifiedUserDto> modifiedUserDto);

}
