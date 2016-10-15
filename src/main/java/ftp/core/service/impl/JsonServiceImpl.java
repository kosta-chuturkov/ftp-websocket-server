package ftp.core.service.impl;

import com.google.gson.Gson;
import ftp.core.model.dto.DataTransferObject;
import ftp.core.model.dto.ResponseModelAdapter;
import ftp.core.service.face.JsonService;
import ftp.core.service.face.tx.FileService;
import ftp.core.websocket.dto.JsonResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * Created by Kosta_Chuturkov on 2/26/2016.
 */
@Service
public class JsonServiceImpl implements JsonService {


    @Resource
    private Gson gson;

    @Resource
    private FileService fileService;


    public JsonResponse getJsonResponse(final String method, final Collection<DataTransferObject> data) {
        final JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setResponseMethod(method);
        jsonResponse.setResult(this.gson.toJson(data));
        return jsonResponse;
    }

    public JSONObject geAstJsonObject(final ResponseModelAdapter dtoWrapper) {
        final JSONObject parent = new JSONObject();
        final JSONArray json = new JSONArray();
        final JSONObject jsonObject = new JSONObject(this.gson.toJson(dtoWrapper));
        json.put(jsonObject.get("abstractJsonResponceDto"));
        parent.put("files", json);
        return parent;
    }
}
