package ftp.core.service.impl;

import com.google.gson.Gson;
import ftp.core.model.dto.DataTransferObject;
import ftp.core.service.face.JsonService;
import ftp.core.service.face.tx.FileService;
import ftp.core.websocket.dto.JsonResponse;
import java.util.Collection;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * Created by Kosta_Chuturkov on 2/26/2016.
 */
@Service
public class JsonServiceImpl implements JsonService {


  @Resource
  private Gson gson;

  @Resource
  private FileService fileService;


  @Override
  public JsonResponse getJsonResponse(final String method,
      final Collection<? extends DataTransferObject> data) {
    final JsonResponse jsonResponse = new JsonResponse();
    jsonResponse.setResponseMethod(method);
    jsonResponse.setResult(this.gson.toJson(data));
    return jsonResponse;
  }
}
