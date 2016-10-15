package ftp.core.controller;

import com.google.gson.Gson;
import ftp.core.common.model.dto.JsonErrorDto;
import ftp.core.common.model.dto.ResponseModelAdapter;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class);
    Gson gson = new Gson();

    public String getAsJson(final ResponseModelAdapter dtoWrapper) {
        final JSONObject parent = new JSONObject();
        final JSONArray json = new JSONArray();
        final JSONObject jsonObject = new JSONObject(this.gson.toJson(dtoWrapper));
        json.put(jsonObject.get("baseFileDto"));
        parent.put("files", json);
        return parent.toString();
    }

    @ExceptionHandler
    public
    @ResponseBody
    ResponseEntity<String> handleException(Exception ex) {
        logger.error("error", ex);
        JsonErrorDto errorDto = new JsonErrorDto.Builder().withError(ex.getMessage()).build();
        ResponseModelAdapter response = new ResponseModelAdapter.Builder().withBaseFileDto(errorDto).build();
        return new ResponseEntity<>(getAsJson(response), HttpStatus.BAD_REQUEST);
    }
}
