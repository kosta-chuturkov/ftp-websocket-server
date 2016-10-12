package ftp.core.service.face;

import java.util.Collection;

import ftp.core.common.model.dto.AbstractDto;
import ftp.core.websocket.dto.JsonResponse;

/**
 * Created by Kosta_Chuturkov on 2/26/2016.
 */
public interface JsonService {

	JsonResponse getJsonResponse(final String method, final Collection<AbstractDto> data);
}
