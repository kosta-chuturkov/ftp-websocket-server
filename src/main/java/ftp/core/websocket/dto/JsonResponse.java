package ftp.core.websocket.dto;

import ftp.core.websocket.handler.Handlers;

/**
 * Created by Kosta_Chuturkov on 2/24/2016.
 */
public class JsonResponse {

    private String responseMethod;

    private String result;

    private String error;

    public JsonResponse() {

    }

    public JsonResponse(final Handlers handlers, final String result) {
        this.responseMethod = handlers.getHandlerName();
        this.result = result;
    }

    public JsonResponse(final String responseMethod, final String error, final String result) {
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

    public String getResult() {
        return this.result;
    }

    public void setResult(final String result) {
        this.result = result;
    }

    public String getError() {
        return this.error;
    }

    public void setError(final String error) {
        this.error = error;
    }
}
