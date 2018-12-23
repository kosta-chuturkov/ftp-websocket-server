package ftp.core.service.face.tx;

import ftp.core.model.dto.FileSharedWithUsersDto;
import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.model.dto.PersonalFileDto;
import ftp.core.model.dto.SharedFileDto;
import ftp.core.model.entities.File;
import ftp.core.service.generic.GenericService;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Transactional
public interface FileService extends GenericService<File, Long> {

  File getFileByDownloadHash(String downloadHash);

  File findByDeleteHash(String deleteHash, String creatorNickName);

  void saveFile(File fileToBeSaved);

  boolean isUserFromFileSharedUsers(Long fileId, String nickName);

  boolean isFileCreator(Long fileId, String userNickName);

  Page<FileSharedWithUsersDto> getFilesISharedWithOtherUsers(String userNickName, Pageable pageable);

  Page<PersonalFileDto> getPrivateFilesForUser(String userNickName, Pageable pageable);

  Page<SharedFileDto> getSharedFilesWithMe(String userNickName, Pageable pageable);

  void updateUsers(final String deleteHash, final Set<ModifiedUserDto> modifiedUserDto);
}
