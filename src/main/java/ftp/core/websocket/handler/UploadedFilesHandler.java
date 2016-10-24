package ftp.core.websocket.handler;

import ftp.core.service.face.FileManagementService;
import ftp.core.service.face.JsonService;
import ftp.core.websocket.dto.JsonRequest;
import ftp.core.websocket.dto.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Kosta_Chuturkov on 2/24/2016.
 */
@Service
public class UploadedFilesHandler extends BaseJsonRequestHandler {

    private FileManagementService fileManagementService;

    @Autowired
    public UploadedFilesHandler(JsonService jsonService, FileManagementService fileManagementService) {
        super(jsonService);
        this.fileManagementService = fileManagementService;
    }

    @Override
    public JsonResponse handleRequestAndReturnJson(final JsonRequest jsonRequest) {
        return super.handle(jsonRequest,
                (firstResult, maxResults) -> this.fileManagementService.getFilesISharedWithOtherUsers(firstResult, maxResults));
    }

    @Override
    public Handlers getHandlerType() {
        return Handlers.FILES_I_SHARED_HANDLER;
    }
}
