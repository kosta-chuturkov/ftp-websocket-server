package ftp.core.listener;

import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import java.util.Map;

/**
 * Created by Kosta_Chuturkov on 2/10/2016.
 */
public class CustomHttpSessionListener implements javax.servlet.http.HttpSessionListener {

    private Logger logger = Logger.getLogger(CustomHttpSessionListener.class);

    private Map<String, HttpSession> httpSessionMap = Maps.newConcurrentMap();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        String sessionId = session.getId();
        if (sessionId != null) {
            httpSessionMap.put(sessionId, session);
        }
        logger.info(se.toString());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        String sessionId = session.getId();
        if (sessionId != null) {
            httpSessionMap.remove(sessionId);
        }
        logger.info(se.toString());
    }

    public HttpSession getHttpSessionById(String sessionId){
        return  httpSessionMap.get(sessionId);
    }
}
