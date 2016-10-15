package ftp.core.common.util;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import ftp.core.common.model.dto.ResponseModelAdapter;
import ftp.core.config.ServerConfigurator;
import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.service.face.tx.FtpServerException;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.security.MessageDigest;
import java.util.Set;

public class ServerUtil {

    public static final Set<String> ALLOWED_EXTENTIONS = Sets.newHashSet("jpg");

    public static final String SALT = "fKWCH(1UafNFK&QK-Vg`FEG(sAE5f^Q.vEA-+Wj?]Sbc+<crP,x]7M/+S}dnb-,^";

    private static final Logger logger = Logger.getLogger(ServerUtil.class);
    private static Gson GSON = new Gson();


    public static boolean userHasSession(final HttpServletRequest request, final boolean checkAttributes)
            throws ServletException, IOException {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        final Cookie[] cookies = request.getCookies();
        if (checkAttributes) {
            final String email = (String) session.getAttribute(ServerConstants.EMAIL_PARAMETER);
            final String password = (String) session.getAttribute(ServerConstants.PASSWORD);
            if (!isEmailValid(email) || !isPasswordValid(password)) {
                return false;
            }
        }
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                final String value = cookies[i].getValue();
                final String name = cookies[i].getName();
                if (name != null && (name.equals(ServerConstants.SESSION_ID_PARAMETER)
                        || name.equals(ServerConstants.JSESSIONID_PARAMETER))) {
                    if (value != null && value.equals(session.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

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

    public static void startUserSession(final HttpServletRequest request, HttpServletResponse response, String nickName, final String email, final String password, final long storage) {
        final HttpSession session = request.getSession();
        final int port = request.getServerPort();
        final String host = request.getServerName();
        final String serverContextAddress = ServerUtil.getProtocol(request) + host + ":" + port;
        final String profilePicAddress = serverContextAddress + APIAliases.PROFILE_PIC_ALIAS;
        session.setAttribute(ServerConstants.EMAIL_PARAMETER, email);
        session.setAttribute(ServerConstants.PASSWORD, password);
        session.setAttribute(ServerConstants.HOST, request.getServerName());
        session.setAttribute(ServerConstants.PORT, request.getServerPort());
        String avatarUrl = getAvatarUrl(serverContextAddress, profilePicAddress, nickName);
        session.setAttribute(ServerConstants.PROFILE_PICTURE_PARAM, avatarUrl);
        session.setAttribute(ServerConstants.STORAGE_PARAMETER, FileUtils.byteCountToDisplaySize(storage));
        session.setAttribute(ServerConstants.MAX_STORAGE_PARAMETER,
                FileUtils.byteCountToDisplaySize(ServerConstants.UPLOAD_LIMIT));
        session.setMaxInactiveInterval(30 * 60);
        response.addCookie(new Cookie(ServerConstants.SESSION_ID_PARAMETER, session.getId()));


    }

    public static String getAvatarUrl(final String serverContextAddress, final String profilePicAddress, final String user) {
        final String avatarUrl;
        final java.io.File file = new java.io.File(ServerConfigurator.getProfilePicsFolder(), user + ".jpg");
        if (file.exists()) {
            avatarUrl = profilePicAddress + user + ".jpg";
        } else {
            avatarUrl = serverContextAddress + "/resources/images/default.jpg";
        }
        return avatarUrl;
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

    public static void deleteFile(final File fileToDelete) {
        if (fileToDelete != null && fileToDelete.exists()) {
            FileDeleteStrategy.FORCE.deleteQuietly(fileToDelete);
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

    public static void sendResourceByName(final HttpServletResponse response, final String filePath, final String fileName) {
        InputStream resIs = null;
        OutputStream osWriter = null;
        try {
            resIs = new FileInputStream(filePath);
            osWriter = response.getOutputStream();
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
        final String contentType = ServerConfigurator.CONTENT_TYPES.get(filePath.substring(filePath.lastIndexOf(".") + 1));
        response.setHeader("Content-Type", contentType == null ? "application/octet-stream" : contentType);
        response.setHeader("Connection", "close");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setHeader("Expires", "0"); // Proxies.
        response.setHeader("Content-Disposition", "inline; filename=" + fileName);
        writeToOsFromIs(osWriter, resIs);
    }

    public static ModelAndView writeToOsFromIs(final OutputStream os, final InputStream is) {
        try {
            final byte[] buffer = new byte[ServerConstants.DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = is.read(buffer, 0, buffer.length)) > 0) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
            os.close();
        } catch (final IOException e) {
            logger.error("errror occured", e);
            throw new FtpServerException("Resource sending failed.");
        }
        return null;
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
