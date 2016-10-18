package ftp.core.repository;

import ftp.core.model.entities.File;
import ftp.core.repository.generic.GenericRepository;

import java.util.List;

public interface FileRepository extends GenericRepository<File, Long> {

    File getFileByDownloadHash(String downloadHash);

    void deleteFile(String deleteHash, String creatorNickName);

    File findByDeleteHash(String deleteHash, String creatorNickName);

    List<File> getSharedFilesForUser(String userNickName, int firstResult, int maxResults);

    List<File> getPrivateFilesForUser(String userNickName, int firstResult, int maxResults);

    List<Long> getSharedFilesWithUsers(Long userId, int firstResult, int maxResults);

    File findWithSharedUsers(Long fileId);
}
