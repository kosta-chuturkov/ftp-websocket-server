package ftp.core.service.face.tx;

import ftp.core.common.model.File;
import ftp.core.persistance.face.generic.service.GenericService;

import java.util.List;
import java.util.Set;

public interface FileService extends GenericService<File, Long> {
    File getFileByDownloadHash(String downloadHash);

    void deleteFile(String deleteHash, String creatorNickName);

    File findByDeleteHash(String deleteHash, String creatorNickName);

    void addUserToFile(Long fileId, String userToAdd);

    void createFileRecord(String fileNameEscaped, long timestamp, int modifier, String userToSendFilesTo, long fileSize,
                          String deleteHash, String downloadHash);

    boolean isUserFromFileSharedUsers(Long fileId, String nickName);

    boolean isFileCreator(Long fileId, String userNickName);

    List<File> getSharedFilesForUser(String userNickName, int firstResult, int maxResults);

    List<File> getPrivateFilesForUser(String userNickName, int firstResult, int maxResults);

    List<Long> getSharedFilesWithUsersIds(Long userId, int firstResult, int maxResults);

    File findWithSharedUsers(Long fileId);

    void updateUsersForFile(final String fileHash, final Set<String> userNickNames);
}
