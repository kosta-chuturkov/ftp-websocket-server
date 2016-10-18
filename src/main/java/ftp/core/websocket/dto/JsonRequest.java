package ftp.core.websocket.dto;

import com.google.gson.JsonObject;

/**
 * Created by Kosta_Chuturkov on 2/24/2016.
 */
public class JsonRequest {

    private String method;

    private JsonObject params;

    public JsonRequest(final String method, final JsonObject params) {
        this.method = method;
        this.params = params;
    }

    public JsonRequest() {

    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    public JsonObject getParams() {
        return this.params;
    }

    public void setParams(final JsonObject params) {
        this.params = params;
    }
}
