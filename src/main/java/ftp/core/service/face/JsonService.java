package ftp.core.service.face;

import ftp.core.model.dto.DataTransferObject;
import ftp.core.websocket.dto.JsonResponse;
import java.util.Collection;
import org.springframework.data.domain.Page;

/**
 * Created by Kosta_Chuturkov on 2/26/2016.
 */
public interface JsonService {

  <T> JsonResponse<T> getJsonResponse(final String method,
      final Page<T> data);
}
