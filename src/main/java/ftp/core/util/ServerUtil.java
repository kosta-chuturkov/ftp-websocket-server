package ftp.core.util;

import com.google.common.hash.Hashing;
import java.nio.charset.Charset;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.core.io.Resource;

public final class ServerUtil {

  private ServerUtil() {
  }

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
    return Hashing.sha256().hashString(payload, Charset.forName("utf-8")).toString();
  }

  public static boolean existsAndIsReadable(Resource resource) {
    return resource.exists() || resource.isReadable();
  }
}
