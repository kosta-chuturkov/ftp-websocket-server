package ftp.core.websocket.dto;

import com.google.gson.JsonObject;

/**
 * Created by Kosta_Chuturkov on 2/24/2016.
 */
public class JsonResponse {

    private String responseMethod;

    private JsonObject result;

    private String error;

    public JsonResponse(){

    }

    public JsonResponse(final String responseMethod, final JsonObject result) {
        this.responseMethod = responseMethod;
        this.result = result;
    }

    public JsonResponse(final String responseMethod, final String error, final JsonObject result) {
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

    public JsonObject getResult() {
        return this.result;
    }

    public void setResult(final JsonObject result) {
        this.result = result;
    }

    public String getError() {
        return this.error;
    }

    public void setError(final String error) {
        this.error = error;
    }
}
