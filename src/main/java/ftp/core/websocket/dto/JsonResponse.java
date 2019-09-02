package ftp.core.websocket.dto;

import java.io.Serializable;
import org.springframework.data.domain.Page;

/**
 * Created by Kosta_Chuturkov on 2/24/2016.
 */
public class JsonResponse<T> implements Serializable{

  private String responseMethod;

  private Page<T> result;

  private String error;

  public JsonResponse() {

  }

  public JsonResponse(final Page<T> result, String handlerName) {
    this.responseMethod = handlerName;
    this.result = result;
  }

  public JsonResponse(final String responseMethod, final String error, final Page<T> result) {
    this.responseMethod = responseMethod;
    this.error = error;
    this.result = result;
  }

  public String getResponseMethod() {
    return this.responseMethod;
  }

  public void setResponseMethod(final String responseMethod) {
    this.responseMethod = responseMethod;
  }

  public Page<T> getResult() {
    return this.result;
  }

  public void setResult(final Page<T> result) {
    this.result = result;
  }

  public String getError() {
    return this.error;
  }

  public void setError(final String error) {
    this.error = error;
  }
}
