package ftp.core.websocket.handler;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import ftp.core.websocket.api.JsonTypedHandler;
import ftp.core.websocket.dto.JsonRequest;
import ftp.core.websocket.dto.JsonResponse;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */
@Service
public class DefaultHandler implements JsonTypedHandler {

    @Override
    public JsonResponse handleRequestAndReturnJson(final JsonRequest jsonRequest) {
        final JsonObject response = new JsonObject();
        response.addProperty("error", "Method not supported.");
        return new JsonResponse<>(new PageImpl<>(Lists.newArrayList(response.toString())), Handlers.DEFAULT_HANDLER.getHandlerName());
    }

    @Override
    public Handlers getHandlerType() {
        return Handlers.DEFAULT_HANDLER;
    }
}
