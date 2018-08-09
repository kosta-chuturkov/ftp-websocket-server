package ftp.core.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ftp.core.config.ApplicationConfig;
import ftp.core.constants.APIAliases;
import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.repository.projections.NickNameProjection;
import ftp.core.security.Authorities;
import ftp.core.service.face.FileManagementService;
import ftp.core.service.face.UserManagementService;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.UserService;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Service("userManagementService")
public class UserManagementServiceImpl implements UserManagementService {

  @Resource
  private UserService userService;
  @Resource
  private FileService fileService;
  @Resource
  private SchedulingService schedulingService;
  @Resource
  private FileManagementService fileManagementService;
  @Autowired
  private ApplicationConfig applicationConfig;

  @Secured(Authorities.USER)
  @RequestMapping(value = {APIAliases.QUERY_USERS_BY_NICK_NAME_ALIAS}, method = RequestMethod.POST)
  public String getUserDetails(final String userNickName) {
    final JsonObject jsonResponse = new JsonObject();
    final JsonArray jsonArrayWrapper = new JsonArray();
    List<NickNameProjection> userByNickLike = this.userService.getUserByNickLike(userNickName);
    userByNickLike
        .forEach(userName -> {
          final JsonObject userObject = new JsonObject();
          userObject.addProperty("id", userName.getNickName());
          userObject.addProperty("full_name", userName.getNickName());
          final JsonObject owner = new JsonObject();
          owner.addProperty("id", Math.random());
          String profilePicUrl = this.fileManagementService
              .getProfilePicUrl(userName.getNickName(), this.applicationConfig.getServerAddress());
          owner.addProperty("avatar_url", profilePicUrl);
          userObject.add("owner", owner);
          jsonArrayWrapper.add(userObject);
        });

    jsonResponse.addProperty("total_count", userByNickLike.size());
    jsonResponse.addProperty("incomplete_results", Boolean.FALSE);
    jsonResponse.add("items", jsonArrayWrapper);

    return jsonResponse.toString();
  }


  @Secured(Authorities.USER)
  @RequestMapping(value = {
      APIAliases.UPDATE_USERS_FILE_IS_SHARED_TO_ALIAS}, method = RequestMethod.POST)
  public void updateUsers(final String deleteHash, final Set<ModifiedUserDto> modifiedUserDto) {
    this.fileService.updateUsers(deleteHash, modifiedUserDto);
  }
}
