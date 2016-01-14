package ftp.core.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;

import ftp.core.config.ServerConfigurator;
import ftp.core.service.face.tx.FtpServerException;

public class ServerUtil {

	public static final String SALT = "fKWCH(1UafNFK&QK-Vg`FEG(sAE5f^Q.vEA-+Wj?]Sbc+<crP,x]7M/+S}dnb-,^";

	private static final Logger logger = Logger.getLogger(ServerUtil.class);

	public static boolean checkUserSession(HttpServletRequest request, boolean checkAttributes)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return false;
		}
		Cookie[] cookies = request.getCookies();
		if (checkAttributes) {
			String email = (String) session.getAttribute(ServerConstants.EMAIL_PARAMETER);
			String password = (String) session.getAttribute(ServerConstants.PASSWORD);
			if (!isEmailValid(email) || !isPasswordValid(password)) {
				return false;
			}
		}
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				String value = cookies[i].getValue();
				String name = cookies[i].getName();
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
	
	public static void removeEmailAndPasswordParams(ModelAndView modelAndView) {
		modelAndView.getModel().remove(ServerConstants.EMAIL_PARAMETER);
		modelAndView.getModel().remove("pswd");
	}

	public static boolean isPasswordValid(String password) {
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

	public static boolean isEmailValid(String email) {
		if (email == null || email.length() < 3 || email.length() > 30) {
			return false;
		}
		return org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(email);
	}

	public static boolean isNickNameValid(String username) {
		if (username != null && username.length() > 3 && username.length() < 30) {
			return username.matches(ServerConstants.USER_REGEX);
		}
		return false;
	}

	public static void startUserSession(HttpServletRequest request, String email, String password, long storage) {
		HttpSession session = request.getSession(true);
		session.setAttribute(ServerConstants.EMAIL_PARAMETER, email);
		session.setAttribute(ServerConstants.PASSWORD, password);
		session.setAttribute(ServerConstants.HOST, request.getServerName());
		session.setAttribute(ServerConstants.PORT, request.getServerPort());
		session.setAttribute(ServerConstants.STORAGE_PARAMETER, FileUtils.byteCountToDisplaySize(storage));
		session.setAttribute(ServerConstants.MAX_STORAGE_PARAMETER,
				FileUtils.byteCountToDisplaySize(ServerConstants.UPLOAD_LIMIT));
		session.setMaxInactiveInterval(30 * 60);

	}

	public static void invalidateSession(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
		// invalidate the session if exists
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}

	public static void deleteFile(File fileToDelete) {
		if (fileToDelete != null && fileToDelete.exists()) {
			FileDeleteStrategy.FORCE.deleteQuietly(fileToDelete);
		}
	}

	public static String hash(String fileName) {
		MessageDigest digest = null;
		StringBuffer buffer = new StringBuffer();
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(fileName.getBytes("UTF-8"));
			for (int i = 0; i < hash.length; i++) {
				buffer.append(String.format("%02x", hash[i]));
			}

		} catch (Exception e) {
			logger.error("errror occured", e);
			return null;
		}
		return buffer.toString();
	}

	public static String digestRawPassword(String password, String salt, String token) {
		return hash(hash(password + salt) + token);
	}

	public static void sendJsonErrorResponce(HttpServletResponse response, String message) {
		response.setContentType("application/json");
		PrintWriter writer;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			throw new FtpServerException(e.getMessage());
		}
		JSONObject parent = new JSONObject();
		JSONArray json = new JSONArray();
		JSONObject jsono = new JSONObject();
		jsono.put("name", "");
		jsono.put("size", "");
		jsono.put("error", message);
		jsono.put("url", "");
		json.put(jsono);
		parent.put("files", json);
		System.out.println(parent.toString());
		writer.write(parent.toString());
		IOUtils.closeQuietly(writer);
	}

	public static final void sendPropertiesAsJson(HttpServletResponse response, Map<String, String> properties) {
		JSONObject parent = new JSONObject();
		JSONArray json = new JSONArray();
		JSONObject jsono = new JSONObject();
		for (Iterator<String> iterator = properties.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			String value = properties.get(key);
			jsono.put(key, value);
		}
		json.put(jsono);
		parent.put("files", json);
		sendJsonMessageToClient(response, parent.toString());
	}

	public static final void sendJsonMessageToClient(HttpServletResponse response, String jsonString) {
		response.setContentType("application/json");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write(jsonString);
		} catch (Exception e) {
			logger.error("Unable to respond with json.", e);
			throw new FtpServerException("Error sending message.");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static String getSessionParam(HttpServletRequest request, String paramName) {
		HttpSession session = request.getSession(false);
		return session == null ? null : (String) session.getAttribute(paramName);
	}

	public static void sendResourceByName(HttpServletResponse response, String filePath, String fileName) {
		InputStream resIs = null;
		OutputStream osWriter = null;
		try {
			resIs = new FileInputStream(filePath);
			osWriter = response.getOutputStream();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		String contentType = ServerConfigurator.CONTENT_TYPES.get(filePath.substring(filePath.lastIndexOf(".") + 1));
		response.setHeader("Content-Type", contentType == null ? "application/octet-stream" : contentType);
		response.setHeader("Connection", "close");
		response.setHeader("Content-Disposition", "inline; filename=" + fileName);
		writeToOsFromIs(osWriter, resIs);
	}

	public static ModelAndView writeToOsFromIs(OutputStream os, InputStream is) {
		try {
			byte buffer[] = new byte[ServerConstants.DEFAULT_BUFFER_SIZE];
			int bytesRead;
			while ((bytesRead = is.read(buffer, 0, buffer.length)) > 0) {
				os.write(buffer, 0, bytesRead);
			}
			os.flush();
			os.close();
		} catch (IOException e) {
			logger.error("errror occured", e);
			throw new FtpServerException("Resource sending failed.");
		}
		return null;
	}

	public static void sendOkResponce(HttpServletResponse response, String fileName, String storedBytes) {
		PrintWriter writer = null;
		JSONObject parent = new JSONObject();
		try {
			response.setContentType("application/json");
			writer = response.getWriter();

			JSONArray json = new JSONArray();

			JSONObject jsono = new JSONObject();
			jsono.put(fileName, "true");
			json.put(jsono);
			parent.put("files", json);
			parent.put("storedBytes", storedBytes);
		} catch (Exception e) {
			logger.error("errror occured", e);
			throw new FtpServerException(e.getMessage());
		} finally {
			if (writer != null) {
				writer.write(parent.toString());
				writer.close();
			}
		}
	}

}
