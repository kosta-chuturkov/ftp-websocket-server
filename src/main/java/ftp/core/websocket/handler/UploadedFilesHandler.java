package ftp.core.websocket.handler;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ftp.core.common.model.File;
import ftp.core.common.model.User;
import ftp.core.common.model.dto.FileDto;
import ftp.core.exception.JsonException;
import ftp.core.service.face.tx.FileService;
import ftp.core.websocket.api.JsonTypedHandler;
import ftp.core.websocket.dto.JsonRequest;
import ftp.core.websocket.dto.JsonResponse;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Kosta_Chuturkov on 2/24/2016.
 */
public class UploadedFilesHandler implements JsonTypedHandler {

    @Resource
    private FileService fileService;

    @Resource
    private Gson gson;

    @Override
    public JsonResponse getJsonResponse(final JsonRequest jsonRequest) {
        final JsonObject params = jsonRequest.getParams();
        final String method = jsonRequest.getMethod();
        final JsonElement firstResult = params.get("firstResult");
        final JsonElement maxResults = params.get("maxResults");
        final List<FileDto> fileDtos = Lists.newArrayList();
        if (firstResult == null || maxResults == null) {
            throw new JsonException("Expected maxResult and firstResult parameters", method);
        }
        final List<File> files = this.fileService.getUploadedFilesForUser(User.getCurrent().getNickName(), firstResult.getAsInt(), maxResults.getAsInt());
        for (final File file : files) {
            final FileDto fileDto = new FileDto(file.getCreator().getNickName(), file.getName(), file.getDownloadHash(),
                    file.getDeleteHash(), file.getFileSize(), file.getTimestamp().toString(), file.getFileType());
            fileDtos.add(fileDto);
        }
        final JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setResponseMethod(method);
        jsonResponse.setResult(this.gson.toJsonTree(fileDtos).getAsJsonObject());
        return jsonResponse;
    }

    @Override
    public String getHandlerType() {
        return HandlerNames.UPLOADED_FILE_HANDLER;
    }
}
