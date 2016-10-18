package ftp.core.websocket.handler;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ftp.core.exception.JsonException;
import ftp.core.model.dto.DataTransferObject;
import ftp.core.model.dto.FileWithSharedUsersDto;
import ftp.core.model.entities.File;
import ftp.core.model.entities.User;
import ftp.core.service.face.JsonService;
import ftp.core.service.face.tx.FileService;
import ftp.core.util.DtoUtil;
import ftp.core.websocket.api.JsonTypedHandler;
import ftp.core.websocket.dto.JsonRequest;
import ftp.core.websocket.dto.JsonResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * Created by Kosta_Chuturkov on 2/24/2016.
 */
@Service
public class SharedWithUsersFilesHandler implements JsonTypedHandler {

    @Resource
    private FileService fileService;

    @Resource
    private JsonService jsonService;

    @Override
    public JsonResponse getJsonResponse(final JsonRequest jsonRequest) {
        final JsonObject params = jsonRequest.getParams();
        final String method = jsonRequest.getMethod();
        final JsonElement firstResult = params.get("firstResult");
        final JsonElement maxResults = params.get("maxResults");
        final List<DataTransferObject> fileDtos = Lists.newArrayList();
        if (firstResult == null || maxResults == null) {
            throw new JsonException("Expected maxResult and firstResult parameters", method);
        }
        final User current = User.getCurrent();
        if (current == null) {
            throw new JsonException("Session has expired. Log in again....", method);
        }
        final Integer firstResultAsInt = firstResult.getAsInt();
        final Integer maxResultsAsInt = maxResults.getAsInt();
        final List<Long> files = this.fileService.getSharedFilesWithUsersIds(current.getId(), firstResultAsInt, maxResultsAsInt);
        for (final Long fileId : files) {
            final File file = this.fileService.findWithSharedUsers(fileId);
            final FileWithSharedUsersDto fileDto = DtoUtil.toFileWithSharedUsersDto(file);
            final Set<String> sharedWithUsers = file.getSharedWithUsers();
            if (sharedWithUsers != null && !sharedWithUsers.isEmpty()) {
                for (final String user : sharedWithUsers) {
                    fileDto.addSharedUser(user);
                }
            }
            fileDtos.add(fileDto);
        }
        return this.jsonService.getJsonResponse(method, fileDtos);
    }

    @Override
    public Handlers getHandlerType() {
        return Handlers.FILES_I_SHARED_HANDLER;
    }
}
