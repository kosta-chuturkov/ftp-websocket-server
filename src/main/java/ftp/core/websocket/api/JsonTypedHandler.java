package ftp.core.websocket.api;

import ftp.core.websocket.dto.JsonRequest;
import ftp.core.websocket.dto.JsonResponse;
import ftp.core.websocket.handler.Handlers;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */
public interface JsonTypedHandler {

  JsonResponse handleRequestAndReturnJson(JsonRequest jsonRequest);

  Handlers getHandlerType();
}
