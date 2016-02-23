package ftp.core.controller;

import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Kosta_Chuturkov on 2/5/2016.
 */
@RestController
public class ImageUploadController {
	//
	// @Autowired
	// private CurrentUserControllerAdvice currentUserControllerAdvice;
	//
	// @Autowired
	// private TechtalksProperties techtalksProperties;
	//
	// @Autowired
	// private SpeakerService speakerService;
	//
	// @RequestMapping(value = {
	// "/speaker/image/**"
	// }, method = RequestMethod.POST)
	// public void logIn(HttpServletRequest request, HttpServletResponse response,
	// @RequestParam("files[]") MultipartFile file, @RequestParam("files[]") String speakerId) throws IOException {
	//
	// final Speaker speaker = speakerService.findByUid(speakerId);
	// if (speaker == null) { throw new RuntimeException("Speaker with id:" + speakerId + " does not exist."); }
	// // saveMultipartFileToDir(request, file);
	// }
	//
	// private void saveMultipartFileToDir(final MultipartFile file, final String targetDir, final String fileName,
	// final boolean deleteFileIfExists) throws IOException {
	//
	// if (file.isEmpty())
	// throw new RuntimeException("You failed to upload " + file.getName() + " because the file was empty.");
	//
	// File targetFolder = new File(techtalksProperties.getStorage().getRootServerStorageLocation(), targetDir);
	// createDirIfNotExist(targetFolder);
	// final File targetFile = new File(targetFolder, fileName);
	// createFile(targetFile, deleteFileIfExists);
	// file.transferTo(targetFile);
	// }
	//
	// private String getDownloadUrl(final String serverContextPath, final String aliasName, final String fileName,
	// final String userId, final String salt) {
	//
	// long currentTime = System.currentTimeMillis();
	// String timestamp = new Long(currentTime).toString();
	// String fileNameAndTimestamp = timestamp + "_" + fileName;
	// String downloadHash = JavaUtilities.hashSHA256(JavaUtilities.hashSHA256(fileNameAndTimestamp + userId) + salt);
	// StringBuilder builder = new StringBuilder();
	// builder.append(serverContextPath);
	// builder.append("/");
	// builder.append(aliasName);
	// builder.append("/");
	// builder.append(downloadHash);
	// return builder.toString();
	// }
	//
	// private String getServerContextPath(final HttpServletRequest request) {
	//
	// int port = request.getServerPort();
	// String host = request.getServerName();
	// String contextPath = request.getContextPath();
	// return getProtocol(request) + host + ":" + port + contextPath;
	// }
	//
	// private void createDirIfNotExist(File clientDir) {
	//
	// if (!clientDir.exists()) {
	// clientDir.mkdir();
	// }
	// }
	//
	// private void createFile(File targetFile, boolean deleteIfExists) {
	//
	// if (targetFile.exists()) {
	// if (deleteIfExists) targetFile.delete();
	// }
	// try {
	// targetFile.createNewFile();
	// } catch (IOException e) {
	// throw new RuntimeException("File with name: '" + targetFile.getName() + "' cannot be created.");
	// }
	//
	// }
	//
	// private String getProtocol(HttpServletRequest request) {
	//
	// boolean isSecure = request.isSecure();
	// String protocol;
	// if (isSecure) {
	// protocol = "https://";
	// } else {
	// protocol = "http://";
	// }
	// return protocol;
	// }

}
