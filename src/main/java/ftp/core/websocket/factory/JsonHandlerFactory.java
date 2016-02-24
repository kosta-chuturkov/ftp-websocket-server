package ftp.core.websocket.factory;

import ftp.core.websocket.api.JsonTypedHandler;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */
public interface JsonHandlerFactory {

    JsonTypedHandler getHandlerByType(String type);
}
