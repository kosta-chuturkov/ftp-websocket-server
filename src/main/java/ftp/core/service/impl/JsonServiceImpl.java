package ftp.core.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ftp.core.common.model.User;
import ftp.core.exception.JsonException;
import ftp.core.service.face.JsonService;
import ftp.core.service.face.tx.FileService;
import ftp.core.websocket.dto.JsonRequest;
import ftp.core.websocket.dto.JsonResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Kosta_Chuturkov on 2/26/2016.
 */
@Service
public class JsonServiceImpl implements JsonService {


    @Resource
    private Gson gson;

    @Resource
    private FileService fileService;


    public JsonResponse getJsonResponse(final String method, final Object data) {
        final JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setResponseMethod(method);
        jsonResponse.setResult(this.gson.toJson(data));
        return jsonResponse;
    }

    public JsonResponse getPagedDataByRange(final JsonRequest jsonRequest, final Object data) {
        final JsonObject params = jsonRequest.getParams();
        final String method = jsonRequest.getMethod();
        final JsonElement firstResult = params.get("firstResult");
        final JsonElement maxResults = params.get("maxResults");
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

        final JsonResponse jsonResponse = getJsonResponse(method, data);
        return jsonResponse;
    }
}
