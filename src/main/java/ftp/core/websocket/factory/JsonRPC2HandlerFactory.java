package ftp.core.websocket.factory;

import ftp.core.websocket.api.JsonRPC2TypedHandler;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */
public interface JsonRPC2HandlerFactory {

    JsonRPC2TypedHandler getHandlerByType(String type);
}
