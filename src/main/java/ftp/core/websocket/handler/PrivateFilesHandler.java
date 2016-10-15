package ftp.core.websocket.handler;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ftp.core.exception.JsonException;
import ftp.core.model.dto.DataTransferObject;
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

/**
 * Created by Kosta_Chuturkov on 2/24/2016.
 */
@Service
public class PrivateFilesHandler implements JsonTypedHandler {

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
        final String nickName = current.getNickName();
        final Integer firstResultAsInt = firstResult.getAsInt();
        final Integer maxResultsAsInt = maxResults.getAsInt();
        final List<File> files = this.fileService.getPrivateFilesForUser(nickName, firstResultAsInt, maxResultsAsInt);
        for (final File file : files) {
            fileDtos.add(DtoUtil.toUploadedFileDto(file));
        }
        return this.jsonService.getJsonResponse(method, fileDtos);
    }


    @Override
    public Handlers getHandlerType() {
        return Handlers.PRIVATE_FILE_HANDLER;
    }
}
