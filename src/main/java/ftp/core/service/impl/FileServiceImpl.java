package ftp.core.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;

import ftp.core.common.model.AbstractEntity;
import ftp.core.common.model.File;
import ftp.core.common.model.User;
import ftp.core.common.model.File.FileType;
import ftp.core.persistance.face.dao.FileDao;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import ftp.core.service.generic.AbstractGenericService;

@Service("fileService")
public class FileServiceImpl extends AbstractGenericService<File, Long> implements FileService {

	@Resource
	private FileDao fileDao;

	@Resource
	private UserService userService;

	@Override
	public File getFileByDownloadHash(String downloadHash) {
		return fileDao.getFileByDownloadHash(downloadHash);
	}

	@Override
	public void deleteFile(String deleteHash, String creatorNickName) {
		fileDao.deleteFile(deleteHash, creatorNickName);
	}

	@Override
	public File findByDeleteHash(String deleteHash, String creatorNickName) {
		return fileDao.findByDeleteHash(deleteHash, creatorNickName);
	}

	@Override
	public void addUserToFile(Number fileId, User userToAdd) {
		AbstractEntity findOne = findOne(fileId);
		if (findOne != null) {
			File file = (File) findOne;
			file.addUser(userToAdd);
			update(file);
		}
	}

	public void createFileRecord(String fileNameEscaped, long timestamp, int modifier, String userToSendFilesTo,
			long fileSize, String deleteHash, String downloadHash) {
		User currentUser = User.getCurrent();
		long remainingStorage = currentUser.getRemainingStorage();
		ftp.core.common.model.File file = new ftp.core.common.model.File();
		file.setName(fileNameEscaped);
		file.setTimestamp(new Date(timestamp));
		file.setDownloadHash(downloadHash);
		file.setDeleteHash(deleteHash);
		file.setFileSize(fileSize);
		file.setCreator(currentUser);
		file.setFileType(ftp.core.common.model.File.FileType.getById(modifier));
		Number savedFileId = save(file);
		if (savedFileId != null) {
			userService.updateRemainingStorageForUser(fileSize, currentUser.getId(), remainingStorage);
			if (modifier == FileType.SHARED.getType()) {
				User userToShareTheFileWith = userService.checkAndGetUserToSendFilesTo(userToSendFilesTo);
				addUserToFile(savedFileId, userToShareTheFileWith);
			}
		} else {
			throw new RuntimeException("Unable to add file!");
		}
	}

	public boolean isUserFromFileSharedUsers(Number fileId, String nickName) {
		AbstractEntity exists = findOne(fileId);
		if (exists == null) {
			return false;
		}
		List<User> sharedWithUsers = ((File) exists).getSharedWithUsers();
		User userByNickName = userService.getUserByNickName(nickName);
		return sharedWithUsers.contains(userByNickName);
	}

	@Override
	public boolean isFileCreator(Number fileId, String userNickName) {
		AbstractEntity exists = findOne(fileId);
		if (exists == null) {
			return false;
		}
		File file = ((File) exists);
		User userByNickName = userService.getUserByNickName(userNickName);
		return file.getCreator().getNickName().equals(userByNickName.getNickName());
	}

	@Override
	public List<File> getSharedFilesForUser(String userNickName, int firstResult, int maxResults) {
		return fileDao.getSharedFilesForUser(userNickName, firstResult, maxResults);
	}

	@Override
	public List<File> getPrivateFilesForUser(String userNickName, int firstResult, int maxResults) {
		return fileDao.getPrivateFilesForUser(userNickName, firstResult, maxResults);
	}

	@Override
	public List<File> getUploadedFilesForUser(String userNickName, int firstResult, int maxResults) {
		return fileDao.getUploadedFilesForUser(userNickName, firstResult, maxResults);
	}

}
