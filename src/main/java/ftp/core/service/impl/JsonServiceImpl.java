package ftp.core.service.impl;

import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import ftp.core.common.model.dto.AbstractDto;
import ftp.core.service.face.JsonService;
import ftp.core.service.face.tx.FileService;
import ftp.core.websocket.dto.JsonResponse;

/**
 * Created by Kosta_Chuturkov on 2/26/2016.
 */
@Service
public class JsonServiceImpl implements JsonService {


    @Resource
    private Gson gson;

    @Resource
    private FileService fileService;


	public JsonResponse getJsonResponse(final String method, final Collection<AbstractDto> data) {
        final JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setResponseMethod(method);
        jsonResponse.setResult(this.gson.toJson(data));
        return jsonResponse;
    }
}
