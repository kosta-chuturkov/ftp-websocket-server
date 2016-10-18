package ftp.core.util;

import com.google.gson.Gson;
import ftp.core.constants.ServerConstants;
import ftp.core.model.dto.ResponseModelAdapter;
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
    private static Gson GSON = new Gson();

    public static String getProtocol(final HttpServletRequest request) {
        final boolean isSecure = request.isSecure();
        final String protocol;
        if (isSecure) {
            protocol = "https://";
        } else {
            protocol = "http://";
        }
        return protocol;
    }

    public static boolean isPasswordValid(final String password) {
        if (password == null) {
            return false;
        }
        if (password.length() < ServerConstants.MINIMUM_PASSWORD_LENGTH) {
            return false;
        }
        if (password.length() > ServerConstants.MAXIMUM_PASSWORD_lENGTH) {
            return false;
        }
        return true;
    }

    public static boolean isEmailValid(final String email) {
        if (email == null || email.length() < 3 || email.length() > 30) {
            return false;
        }
        return org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(email);
    }

    public static boolean isNickNameValid(final String username) {
        if (username != null && username.length() > 3 && username.length() < 30) {
            return username.matches(ServerConstants.USER_REGEX);
        }
        return false;
    }


    public static String getServerContextAddress(HttpServletRequest request) {
        final int port = request.getServerPort();
        final String host = request.getServerName();
        return getProtocol(request) + host + ":" + port;
    }

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

    public static JSONObject geAstJsonObject(final ResponseModelAdapter dtoWrapper) {
        final JSONObject parent = new JSONObject();
        final JSONArray json = new JSONArray();
        final JSONObject jsonObject = new JSONObject(GSON.toJson(dtoWrapper));
        json.put(jsonObject.get("baseFileDto"));
        parent.put("files", json);
        return parent;
    }

    public static String getSessionParam(final HttpServletRequest request, final String paramName) {
        final HttpSession session = request.getSession(false);
        return session == null ? null : (String) session.getAttribute(paramName);
    }

    public static void sendOkResponce(final HttpServletResponse response, final String fileName, final String storedBytes) {
        PrintWriter writer = null;
        final JSONObject parent = new JSONObject();
        try {
            response.setContentType("application/json");
            writer = response.getWriter();

            final JSONArray json = new JSONArray();

            final JSONObject jsono = new JSONObject();
            jsono.put(fileName, "true");
            json.put(jsono);
            parent.put("files", json);
            parent.put("storedBytes", storedBytes);
        } catch (final Exception e) {
            throw new FtpServerException(e.getMessage());
        } finally {
            if (writer != null) {
                writer.write(parent.toString());
                writer.close();
            }
        }
    }
}
