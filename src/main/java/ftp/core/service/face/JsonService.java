package ftp.core.service.face;

import ftp.core.common.model.dto.DataTransferObject;
import ftp.core.common.model.dto.ResponseModelAdapter;
import ftp.core.websocket.dto.JsonResponse;
import org.json.JSONObject;

import java.util.Collection;

/**
 * Created by Kosta_Chuturkov on 2/26/2016.
 */
public interface JsonService {

	JsonResponse getJsonResponse(final String method, final Collection<DataTransferObject> data);
	JSONObject geAstJsonObject(final ResponseModelAdapter dtoWrapper);
}
