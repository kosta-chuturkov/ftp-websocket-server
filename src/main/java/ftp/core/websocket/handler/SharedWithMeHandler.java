package ftp.core.websocket.handler;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ftp.core.common.model.File;
import ftp.core.common.model.User;
import ftp.core.common.model.dto.FileDto;
import ftp.core.common.model.dto.SharedFileDto;
import ftp.core.exception.JsonException;
import ftp.core.service.face.JsonService;
import ftp.core.service.face.tx.FileService;
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
public class SharedWithMeHandler implements JsonTypedHandler {

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
        final List<FileDto> fileDtos = Lists.newArrayList();
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
        final List<File> files = this.fileService.getSharedFilesForUser(nickName, firstResultAsInt, maxResultsAsInt);
        for (final File file : files) {
            final FileDto fileDto = new SharedFileDto(file.getCreator().getNickName(), file.getName(), file.getDownloadHash(),
                    file.getFileSize(), file.getTimestamp().toString(), file.getFileType());
            fileDtos.add(fileDto);
        }
        return this.jsonService.getJsonResponse(method, fileDtos);
    }

    @Override
    public Handlers getHandlerType() {
        return Handlers.FILES_SHARED_WITH_ME_HANDLER;
    }
}
