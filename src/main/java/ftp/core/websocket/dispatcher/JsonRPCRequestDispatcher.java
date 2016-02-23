package ftp.core.websocket.dispatcher;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Parser;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

import ftp.core.exception.JsonRPC2Exception;
import ftp.core.listener.WebSocketSessionListener;
import ftp.core.websocket.api.JsonRPC2TypedHandler;
import ftp.core.websocket.factory.JsonRPC2HandlerFactory;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */
public class JsonRPCRequestDispatcher extends TextWebSocketHandler {

    private final Logger logger = Logger.getLogger(JsonRPCRequestDispatcher.class);

    @Resource
    private WebSocketSessionListener webSocketSessionListener;

    @Resource
    private JsonRPC2HandlerFactory jsonRPC2HandlerFactory;

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
      if(this.webSocketSessionListener.addSession(session)) {
          this.logger.debug("Web Socket session added: " + session.toString());
      }else {
          this.logger.debug("Session with this id exists.Closing session: " + session.toString());
          session.close(CloseStatus.NOT_ACCEPTABLE);
      }
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {
        this.webSocketSessionListener.removeSession(session);
        this.logger.debug("Web Socket session removed: "+ session.toString());
    }

    @Override
    public void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {
        JSONRPC2Request jsonrpc2Request = null;
       try {
           final JSONRPC2Parser jsonrpc2Parser = new JSONRPC2Parser();
           jsonrpc2Request = jsonrpc2Parser.parseJSONRPC2Request(message.getPayload());
           final String methodToHandle = jsonrpc2Request.getMethod();
           final JsonRPC2TypedHandler handlerByType = this.jsonRPC2HandlerFactory.getHandlerByType(methodToHandle);
           final JSONRPC2Response jsonrpc2Response = handlerByType.getJSONRPC2Response(jsonrpc2Request);
           final String response = jsonrpc2Response.toJSONString();
           session.sendMessage(new TextMessage(response));
       }catch (final Exception e){
           this.logger.error(e);
           final JSONRPC2Response jsonrpc2Response;
           if(jsonrpc2Request == null) {
               jsonrpc2Response = new JSONRPC2Response(JSONRPC2Error.PARSE_ERROR);
           }else {
               if(e instanceof JsonRPC2Exception) {
                   final JsonRPC2Exception jsonRPC2Exception = (JsonRPC2Exception) e;
                   jsonrpc2Response = new JSONRPC2Response(jsonRPC2Exception.getJsonRPC2Error(), jsonRPC2Exception.getObjectId());
               }else {
                   jsonrpc2Response = new JSONRPC2Response(JSONRPC2Error.INTERNAL_ERROR, jsonrpc2Request.getID());
                   jsonrpc2Response.setResult(e.getMessage());
               }
           }
           session.sendMessage(new TextMessage(jsonrpc2Response.toJSONString()));
       }
    }

}
