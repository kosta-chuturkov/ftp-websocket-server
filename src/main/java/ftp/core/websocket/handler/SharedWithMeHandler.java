package ftp.core.websocket.handler;

import ftp.core.service.face.FileManagementService;
import ftp.core.service.face.JsonService;
import ftp.core.websocket.dto.JsonRequest;
import ftp.core.websocket.dto.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Created by Kosta_Chuturkov on 2/24/2016.
 */
@Service
public class SharedWithMeHandler extends BaseJsonRequestHandler {


  private FileManagementService fileManagementService;

  @Autowired
  public SharedWithMeHandler(JsonService jsonService, FileManagementService fileManagementService) {
    super(jsonService);
    this.fileManagementService = fileManagementService;
  }

  @Override
  public JsonResponse handleRequestAndReturnJson(final JsonRequest jsonRequest) {
    return super.handle(jsonRequest,
        (firstResult, maxResults) ->
            this.fileManagementService.getFilesSharedToMe(new PageRequest(firstResult, maxResults)));
  }

  @Override
  public Handlers getHandlerType() {
    return Handlers.FILES_SHARED_WITH_ME_HANDLER;
  }
}
