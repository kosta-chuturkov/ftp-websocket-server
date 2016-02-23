package ftp.core.exception;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */
public class JsonRPC2Exception extends RuntimeException {

    private final JSONRPC2Error jsonRPC2Error;

    private final String objectId;

    public JsonRPC2Exception(final String message, final JSONRPC2Error jsonrpc2Error, final String objectId){
        super(message);
        this.jsonRPC2Error = jsonrpc2Error;
        this.objectId = objectId;
    }

    public JSONRPC2Error getJsonRPC2Error() {
        return this.jsonRPC2Error;
    }

    public String getObjectId() {
        return this.objectId;
    }
}
