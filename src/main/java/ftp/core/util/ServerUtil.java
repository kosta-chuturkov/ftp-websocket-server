package ftp.core.util;

import java.security.MessageDigest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

public final class ServerUtil {

  private ServerUtil() {
  }

  private static final Logger logger = Logger.getLogger(ServerUtil.class);

  public static void invalidateSession(final HttpServletRequest request,
      final HttpServletResponse response) {
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

  public static String hashSHA256(final String payload) {
    MessageDigest digest;
    final StringBuffer buffer;
    try {
      digest = MessageDigest.getInstance("SHA-256");
      final byte[] hash = digest.digest(payload.getBytes("UTF-8"));
      buffer = new StringBuffer(hash.length);
      for (byte aHash : hash) {
        buffer.append(String.format("%02x", aHash));
      }
      return buffer.toString();
    } catch (final Exception e) {
      logger.error("errror occured", e);
      throw new RuntimeException(e);
    }
  }

  public static boolean existsAndIsReadable(Resource resource) {
    return resource.exists() || resource.isReadable();
  }
}
