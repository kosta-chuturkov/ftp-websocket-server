package ftp.core.persistance.face.dao;

import java.util.List;

import ftp.core.common.model.File;
import ftp.core.persistance.face.generic.dao.GenericDao;

public interface FileDao extends GenericDao<File, Long> {

	File getFileByDownloadHash(String downloadHash);

	void deleteFile(String deleteHash, String creatorNickName);

	File findByDeleteHash(String deleteHash, String creatorNickName);

	List<File> getSharedFilesForUser(String userNickName, int firstResult, int maxResults);
	
	List<File> getPrivateFilesForUser(String userNickName, int firstResult, int maxResults);
	
	List<File> getUploadedFilesForUser(String userNickName, int firstResult, int maxResults);
}
