package ftp.core.websocket.api;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */
public interface JsonRPC2TypedHandler {

    JSONRPC2Response getJSONRPC2Response(JSONRPC2Request jsonrpc2Request);

    String getHandlerType();
}
