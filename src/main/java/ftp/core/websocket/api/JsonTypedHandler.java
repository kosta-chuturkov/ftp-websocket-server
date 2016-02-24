package ftp.core.websocket.api;

import ftp.core.websocket.dto.JsonRequest;
import ftp.core.websocket.dto.JsonResponse;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */
public interface JsonTypedHandler {

    JsonResponse getJsonResponse(JsonRequest jsonRequest);

    String getHandlerType();
}
