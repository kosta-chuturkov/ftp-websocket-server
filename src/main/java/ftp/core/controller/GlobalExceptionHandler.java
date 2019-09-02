package ftp.core.controller;

import com.google.gson.Gson;
import ftp.core.model.dto.JsonErrorDto;
import ftp.core.model.dto.ResponseModelAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private Gson gson;

  @Autowired
  public GlobalExceptionHandler(Gson gson) {
    this.gson = gson;
  }

  public String getExpectedAsJsonModelFromClient(String message, String storedBytes)
      throws JSONException {
    final JSONObject parent = new JSONObject();
    final JSONArray json = new JSONArray();
    JsonErrorDto errorDto = new JsonErrorDto.Builder().withError(message).build();
    ResponseModelAdapter response = new ResponseModelAdapter.Builder().withBaseFileDto(errorDto)
        .withStoredBytes(storedBytes).build();
    final JSONObject jsonObject = new JSONObject(this.gson.toJson(response));
    json.put(jsonObject.get("baseFileDto"));
    parent.put("files", json);
    return parent.toString();
  }

  @ExceptionHandler
  public
  @ResponseBody
  ResponseEntity<String> handleException(Exception ex) throws JSONException {
    logger.error("error", ex);
    return new ResponseEntity<>(getExpectedAsJsonModelFromClient(ex.getMessage(), null),
        HttpStatus.BAD_REQUEST);
  }
}
