package ftp.core.service.face;

import ftp.core.model.dto.DataTransferObject;
import ftp.core.websocket.dto.JsonResponse;
import java.util.Collection;

/**
 * Created by Kosta_Chuturkov on 2/26/2016.
 */
public interface JsonService {

  JsonResponse getJsonResponse(final String method,
      final Collection<? extends DataTransferObject> data);
}
