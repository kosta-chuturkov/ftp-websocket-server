package ftp.core.service.face;

import ftp.core.websocket.dto.JsonRequest;
import ftp.core.websocket.dto.JsonResponse;

/**
 * Created by Kosta_Chuturkov on 2/26/2016.
 */
public interface JsonService {

    JsonResponse getJsonResponse(final String method, final Object data);

    JsonResponse getPagedDataByRange(final JsonRequest jsonRequest, final Object data);
}
