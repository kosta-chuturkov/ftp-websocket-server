package ftp.core.websocket.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ftp.core.exception.JsonException;
import ftp.core.model.dto.DataTransferObject;
import ftp.core.model.entities.User;
import ftp.core.service.face.JsonService;
import ftp.core.websocket.api.JsonTypedHandler;
import ftp.core.websocket.dto.JsonRequest;
import ftp.core.websocket.dto.JsonResponse;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by Kosta_Chuturkov on 2/24/2016.
 */
public abstract class BaseJsonRequestHandler implements JsonTypedHandler {

    protected final JsonService jsonService;

    public BaseJsonRequestHandler(JsonService jsonService) {
        this.jsonService = jsonService;
    }

    protected <T extends DataTransferObject> JsonResponse handle(final JsonRequest jsonRequest, BiFunction<Integer, Integer, List<T>> function) {
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
        final Integer firstResultAsInt = firstResult.getAsInt();
        final Integer maxResultsAsInt = maxResults.getAsInt();
        final List<T> fileDtos = function.apply(firstResultAsInt, maxResultsAsInt);
        return this.jsonService.getJsonResponse(method, fileDtos);
    }
}
