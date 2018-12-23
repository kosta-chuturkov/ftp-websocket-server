package ftp.core.service.impl;

import ftp.core.service.face.JsonService;
import ftp.core.websocket.dto.JsonResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Created by Kosta_Chuturkov on 2/26/2016.
 */
@Service
public class JsonServiceImpl implements JsonService {

  @Override
  public <T> JsonResponse<T> getJsonResponse(final String method,
      final Page<T> data) {
    final JsonResponse<T> jsonResponse = new JsonResponse<>();
    jsonResponse.setResponseMethod(method);
    jsonResponse.setResult(data);
    return jsonResponse;
  }
}
