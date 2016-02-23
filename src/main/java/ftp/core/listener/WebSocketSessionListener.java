package ftp.core.listener;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.google.common.collect.Maps;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */

@Service("webSocketSessionListener")
public class WebSocketSessionListener {

    private final Map<String, WebSocketSession> webSocketSessions = Maps.newConcurrentMap();

    /**
     * Returns false if session exists already.
     * @param webSocketSession
     */
    public final boolean addSession(final WebSocketSession webSocketSession){
        final String webSocketSessionId = webSocketSession.getId();

        if(webSocketSessionId == null || this.webSocketSessions.get(webSocketSessionId)!=null){
         return false;
        }
        this.webSocketSessions.put(webSocketSessionId,webSocketSession);
        return  true;
    }

    public final WebSocketSession removeSession(final WebSocketSession webSocketSession){
        final String webSocketSessionId = webSocketSession.getId();
        if(webSocketSessionId != null) {
            return this.webSocketSessions.remove(webSocketSessionId);
        }
        return null;
    }

}
