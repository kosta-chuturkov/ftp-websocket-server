package ftp.core.websocket.handler;

import org.springframework.stereotype.Service;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import ftp.core.websocket.api.JsonRPC2TypedHandler;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */
@Service
public class DefaultHandler implements JsonRPC2TypedHandler{
    @Override
    public JSONRPC2Response getJSONRPC2Response(final JSONRPC2Request jsonrpc2Request) {
        return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, jsonrpc2Request.getID());
    }

    @Override
    public String getHandlerType() {
        return HandlerNames.DEFAULT_HANDLER_NAME;
    }
}
