package ftp.core.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;

import ftp.core.common.model.User;
import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;
import ftp.core.config.ServerConfigurator;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;

@Controller
public class UploadController {

	private static final Logger logger = Logger.getLogger(UploadController.class);
    @Resource
	private FileService fileService;
	@Resource
    private UserService userService;

    @RequestMapping(value = {"/upload**"}, method = RequestMethod.GET)
    public ModelAndView getLoginPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (ServerUtil.checkUserSession(request, true)) {
                return new ModelAndView(ServerConstants.UPLOAD_PAGE);
            } else {
                ServerUtil.invalidateSession(request, response);
                ModelAndView modelAndView = new ModelAndView("redirect:" + ServerConstants.LOGIN_ALIAS);
                return modelAndView;
            }
        } catch (Exception e) {
            throw new FtpServerException(e.getMessage());
        }
    }

    @RequestMapping(value = {"/upload**"}, method = RequestMethod.POST)
	public void logIn(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("files[]") MultipartFile file, @RequestParam("modifier") String modifier,
			@RequestParam("nickName") String nickName) throws IOException {
        String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
        String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
        User current = userService.findByEmailAndPassword(email, password);
        if (current == null) {
            ServerUtil.sendJsonErrorResponce(response, "You must login first.");
        } else {
            User.setCurrent(current);
            if (!file.isEmpty()) {
                try {
					int port = request.getServerPort();
					String host = request.getServerName();
					String contextPath = request.getContextPath();
					String serverContextAddress = getProtocol(request) + host + ":" + port + contextPath
							+ ServerConstants.FILES_ALIAS;
                    BigDecimal token = current.getToken();
					String fileName = StringEscapeUtils.escapeSql(file.getOriginalFilename());
                    long currentTime = System.currentTimeMillis();
                    String tempFileName = new Long(currentTime).toString();
                    String serverFileName = tempFileName + "_" + fileName;
                    String deleteHash = ServerUtil.hash(ServerUtil.hash(serverFileName + token) + ServerConstants.DELETE_SALT);
                    String downloadHash = ServerUtil.hash((serverFileName + token) + ServerConstants.DOWNLOAD_SALT);
					fileService.createFileRecord(fileName, currentTime, getModifier(modifier), nickName, file.getSize(),
                            deleteHash, downloadHash);
                    File userFolder = getUserFolder(current.getEmail());
                    final File targetFile = new File(userFolder, serverFileName);
                    if (targetFile.exists()) {
                        targetFile.delete();
                    } else {
                        targetFile.createNewFile();
                    }

                    file.transferTo(targetFile);
					Map<String, String> jsono = Maps.newHashMap();
					jsono.put("name", StringEscapeUtils.escapeHtml(serverFileName));
					jsono.put("size", Long.toString(file.getSize()));
					jsono.put("url", (serverContextAddress + downloadHash));
					jsono.put("thumbnail_url", "");
					jsono.put("deleteUrl", (serverContextAddress + ServerConstants.DELETE_ALIAS + deleteHash));
					jsono.put("deleteType", "GET");
					ServerUtil.sendPropertiesAsJson(response, jsono);

                } catch (Exception e) {
					if (e instanceof HibernateException) {
						ServerUtil.sendJsonErrorResponce(response, "Unexpected error occured. Try again.");
					} else {
						ServerUtil.sendJsonErrorResponce(response, e.getMessage());
					}
                }
            } else {
				ServerUtil.sendJsonErrorResponce(response,
						"You failed to upload " + file.getName() + " because the file was empty.");
            }
            //uploadFile(request, response);
        }
    }

    private void printHeaderNames(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String nextElement = headerNames.nextElement();
            logger.info(nextElement + "||" + request.getHeader(nextElement));
        }
    }

	/*
	 * public void uploadFile(HttpServletRequest request, HttpServletResponse response) {
	 * printHeaderNames(request);
	 * File tempFile = null;
	 * User currentUser = User.getCurrent();
	 * BigDecimal token = currentUser.getToken();
	 * InputStream clientInputStream = null;
	 * Hashtable<String, String> headers = new Hashtable<String, String>();
	 * int port = request.getServerPort();
	 * String host = request.getServerName();
	 * String contextPath = request.getContextPath();
	 * String serverContextAddress = getProtocol(request) + host + ":" + port + contextPath
	 * + ServerConstants.FILES_ALIAS;
	 * try {
	 * clientInputStream = request.getInputStream();
	 * String boundry = getBoundry(request.getContentType());
	 * long multipartHeadersLength = readMultiPartHeaders(headers, clientInputStream, boundry);
	 * String userToSendFilesTo = headers.get(ServerConstants.NICK_NAME_PARAMETER);
	 * String fileNameEscaped = getFileName(headers);
	 * int modifier = getModifier(headers);
	 * long currentTime = System.currentTimeMillis();
	 * String tempFileName = new Long(currentTime).toString();
	 * String serverFileName = tempFileName + "_" + fileNameEscaped;
	 * 
	 * long contentLength = getContentLength(request.getHeader("Content-Length"), multipartHeadersLength);
	 * long remainingBytes = currentUser.getRemainingStorage();
	 * 
	 * if ((remainingBytes - contentLength) < 0) {
	 * IOUtils.closeQuietly(clientInputStream);
	 * throw new FtpServerException("You are exceeding your upload limit:"
	 * + FileUtils.byteCountToDisplaySize(ServerConstants.UPLOAD_LIMIT) + ". You have: "
	 * + FileUtils.byteCountToDisplaySize(remainingBytes) + " remainig storage.");
	 * }
	 * File userFolder = getUserFolder(currentUser.getEmail());
	 * tempFile = new File(userFolder, tempFileName);
	 * FileOutputStream outputStream = new FileOutputStream(tempFile);
	 * long fileSize = readResponceContents(contentLength, outputStream, clientInputStream, boundry,
	 * remainingBytes);
	 * contentLength -= boundry.length() + 32;
	 * if (fileSize != contentLength) {
	 * throw new FtpServerException("Sending:" + (contentLength) + ", but recived:" + fileSize);
	 * }
	 * String deleteHash = ServerUtil.hash(ServerUtil.hash(serverFileName + token) + ServerConstants.DELETE_SALT);
	 * String downloadHash = ServerUtil.hash((serverFileName + token) + ServerConstants.DOWNLOAD_SALT);
	 * fileService.createFileRecord(fileNameEscaped, currentTime, modifier, userToSendFilesTo, fileSize,
	 * deleteHash, downloadHash);
	 * tempFile.renameTo(new File(userFolder, serverFileName));
	 * 
	 * Map<String, String> jsono = Maps.newHashMap();
	 * jsono.put("name", StringEscapeUtils.escapeHtml(fileNameEscaped));
	 * jsono.put("size", Long.toString(fileSize));
	 * jsono.put("url", (serverContextAddress + downloadHash));
	 * jsono.put("thumbnail_url", "");
	 * jsono.put("deleteUrl", (serverContextAddress + ServerConstants.DELETE_ALIAS + deleteHash));
	 * jsono.put("deleteType", "GET");
	 * ServerUtil.sendPropertiesAsJson(response, jsono);
	 * } catch (Exception e) {
	 * logger.error("errror occured", e);
	 * ServerUtil.deleteFile(tempFile);
	 * if (e instanceof HibernateException) {
	 * ServerUtil.sendJsonErrorResponce(response, "Unexpected error occured. Try again.");
	 * } else {
	 * ServerUtil.sendJsonErrorResponce(response, e.getMessage());
	 * }
	 * }
	 * }
	 */

    private String getFileName(Hashtable<String, String> headers) {
        String fileName = StringEscapeUtils.escapeSql(headers.get(ServerConstants.FILE_NAME_PARAMETER));
        if (fileName == null) {
            throw new FtpServerException("File name not specified.");
        }
        if (fileName.length() < 3 || fileName.length() > ServerConstants.MAX_FILE_NAME_LENGTH) {
            throw new FtpServerException(
                    "File name is longer then " + ServerConstants.MAX_FILE_NAME_LENGTH + " symbols!");
        }
        return fileName;
    }

	private int getModifier(String modifierString) throws IOException {
        int modifier = -1;
        try {
			modifier = Integer.parseInt(modifierString);
            if (checkModifier(modifier)) {
                throw new FtpServerException(
					"Modifier parameter is incorrect:" + modifierString + ".");
            }
        } catch (NumberFormatException e) {
            throw new FtpServerException(
					"Modifier parameter is incorrect:" + modifierString + ":" + ".The supported type is int.");
        }
        return modifier;
    }

    private long getContentLength(String contentLengthStr, long multipartHeadersLength) throws IOException {
        long contentLength;
        try {
            if (contentLengthStr == null || contentLengthStr.length() == 0) {
                throw new FtpServerException("Missing Content-Length parameter!");
            }
            contentLength = Long.parseLong(contentLengthStr);
            contentLength -= multipartHeadersLength;
        } catch (NumberFormatException e) {
            throw new FtpServerException("Content-Length is invalidly formated or exeeds the maximum permitted size!");
        }
        return contentLength;
    }

    private boolean checkModifier(int modifier) {
        return ftp.core.common.model.File.FileType.getById(modifier) == null;
    }

    private String getFileNameFromCD(String contentDisposition) {
        String[] parsedDisposition = contentDisposition.split(";");

        String name = parsedDisposition[1].trim().split("=")[1].replace("\"", "");
        if (name.equals("files[]") && parsedDisposition.length > 2) {
            String fileNameUnparsed = parsedDisposition[2].trim().split("=")[1].replace("\"", "");
            return fileNameUnparsed;
        }
        return null;
    }

    private String getBoundry(String contentType) {

        String[] parsedDisposition = contentType.split(" ");
        if (parsedDisposition.length < 2) {
            return null;
        }
        String boudryUnparsed = parsedDisposition[1].split("=")[1];
        return boudryUnparsed;
    }

    private long readMultiPartHeaders(Hashtable<String, String> table, InputStream clientInputStream, String boundry) {
        String line = null;
        int counter = 0;
        long multiPartHeadersLength = 0;
        while (counter < 12) {
            line = readLine(clientInputStream);
            multiPartHeadersLength += line.length();
            if (counter == 3) {
                table.put(ServerConstants.MODIFIER_PARAMETER, line);
            }
            if (counter == 7) {
                table.put(ServerConstants.NICK_NAME_PARAMETER, line.trim());
            }
            if (counter == 9) {
                int position = line.indexOf("Content-Disposition");
                if (position != -1) {
                    String fileName = getFileNameFromCD(line.substring(position + 1));
                    if (fileName != null) {
                        table.put("fileName", fileName);
                    }
                }
            }
            counter++;
        }
        return multiPartHeadersLength;
    }

    private String readLine(InputStream in) {
        try {
            int byteRead;
            StringBuffer currentLine = new StringBuffer();
            while ((byteRead = in.read()) != -1) {
                if (byteRead == '\r') {
                    int next = in.read();
                    if (next == '\n') {
                        break;
                    }
                }
                currentLine.append((char) byteRead);
            }
            return currentLine.toString();
        } catch (IOException e) {
            logger.error("errror occured", e);
        }
        return "";
    }

    private int getBufferSize(long contentLength) {
        if (contentLength < ServerConstants.TWENTY_MEGABYTES) {
            return 1024;
        }
        if (contentLength > ServerConstants.TWENTY_MEGABYTES
                && contentLength < ServerConstants.FIVE_HUNDRED_MEGABYTES) {
            return 2048;
        }
        if (contentLength > ServerConstants.FIVE_HUNDRED_MEGABYTES) {
            return 8192;
        }
        return 1024;
    }

    private long readResponceContents(long contentLength, FileOutputStream responseFileStream,
                                      InputStream clientInputStream, String boundry, long storedBytes) {
        try {
            int bufferSize = getBufferSize(contentLength);
            int boundryLength = boundry.length() + 8;// aditional 8 bytes at the
            // end of the file that
            // need to be removed.
            byte oddBuffer[] = new byte[bufferSize];
            byte eavenBuffer[] = new byte[bufferSize];
            long iterations = contentLength / bufferSize;
            long counter = 0;
            int byteRead = 0;
            int lastBytes = 0;
            long contentSize = 0;
            while (contentSize < contentLength) {

                if (iterations == counter) {
                    byteRead = clientInputStream.read(oddBuffer, 0, bufferSize);
                    lastBytes = clientInputStream.read(eavenBuffer, 0, bufferSize);
                    if (lastBytes <= 0) {
                        byteRead -= (boundryLength);
                        contentSize += byteRead;
                        responseFileStream.write(oddBuffer, 0, byteRead);
                    } else if (lastBytes < boundryLength) {
                        byteRead -= ((boundryLength) - lastBytes);
                        contentSize += byteRead;
                        responseFileStream.write(oddBuffer, 0, byteRead);
                    } else {
                        responseFileStream.write(oddBuffer, 0, byteRead);
                        responseFileStream.write(eavenBuffer, 0, lastBytes - (boundryLength));
                        contentSize += byteRead + (lastBytes - (boundryLength));
                    }
                    break;
                }

                byteRead = clientInputStream.read(oddBuffer, 0, bufferSize);
                if (byteRead <= 0) {
                    break;
                }
                contentSize += byteRead;
                responseFileStream.write(oddBuffer, 0, byteRead);
                counter++;
                checkContentSize(contentSize, storedBytes, clientInputStream);
            }
            checkContentSize(contentSize, storedBytes, clientInputStream);
            return contentSize;
        } catch (IOException e) {
            logger.error("errror occured", e);
        } finally {
            IOUtils.closeQuietly(responseFileStream);
        }
        return -1;
    }

    private void checkContentSize(long contentSize, long storedBytes, InputStream clientInputStream) {
        if (contentSize > ServerConstants.MAX_FILE_SIZE) {
            String message = "You have exceeded the maximum file size allowed: "
                    + FileUtils.byteCountToDisplaySize(ServerConstants.MAX_FILE_SIZE) + " .";
            closeClientIsAndThrowExc(clientInputStream, message);
            throw new IllegalArgumentException(message);
        }
        if (contentSize > storedBytes) {
            String message = "You have exceeded the maximum storage size.You have "
                    + FileUtils.byteCountToDisplaySize(storedBytes) + " remaining .";
            closeClientIsAndThrowExc(clientInputStream, message);
        }
    }

    private void closeClientIsAndThrowExc(InputStream clientInputStream, String message) {
        IOUtils.closeQuietly(clientInputStream);
        throw new IllegalArgumentException(message);
    }

    private File getUserFolder(String userName) {
        File clientDir = new File(ServerConfigurator.getServerStorageFile(), userName);
        createUserDirIfNotExist(clientDir);
        return clientDir;
    }

    private void createUserDirIfNotExist(File clientDir) {
        if (!clientDir.exists()) {
            clientDir.mkdir();
        }
    }

    private String getProtocol(HttpServletRequest request) {
        boolean isSecure = request.isSecure();
        String protocol;
        if (isSecure) {
            protocol = "https://";
        } else {
            protocol = "http://";
        }
        return protocol;
    }

}
