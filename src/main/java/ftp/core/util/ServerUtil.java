package ftp.core.util;

import ftp.core.service.face.tx.FtpServerException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.security.MessageDigest;

public final class ServerUtil {

    private ServerUtil() {
    }

    private static final Logger logger = Logger.getLogger(ServerUtil.class);

    public static void invalidateSession(final HttpServletRequest request, final HttpServletResponse response) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
        // invalidate the session if exists
        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public static String hash(final String fileName) {
        MessageDigest digest;
        final StringBuffer buffer = new StringBuffer();
        try {
            digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(fileName.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++) {
                buffer.append(String.format("%02x", hash[i]));
            }

        } catch (final Exception e) {
            logger.error("errror occured", e);
            return null;
        }
        return buffer.toString();
    }
}
