package ftp.core.service.face.tx;

import java.util.List;

import ftp.core.common.model.AbstractEntity;
import ftp.core.common.model.File;
import ftp.core.common.model.User;
import ftp.core.persistance.face.generic.service.GenericService;

public interface FileService extends GenericService<AbstractEntity, Number> {
	File getFileByDownloadHash(String downloadHash);

	void deleteFile(String deleteHash, String creatorNickName);

	File findByDeleteHash(String deleteHash, String creatorNickName);

	void addUserToFile(Number fileId, User userToAdd);

	void createFileRecord(String fileNameEscaped, long timestamp, int modifier, String userToSendFilesTo, long fileSize,
			String deleteHash, String downloadHash);

	boolean isUserFromFileSharedUsers(Number fileId, String nickName);

	boolean isFileCreator(Number fileId, String userNickName);

	List<File> getSharedFilesForUser(String userNickName, int firstResult, int maxResults);
	
	List<File> getPrivateFilesForUser(String userNickName, int firstResult, int maxResults);
	
	List<File> getUploadedFilesForUser(String userNickName, int firstResult, int maxResults);
}
