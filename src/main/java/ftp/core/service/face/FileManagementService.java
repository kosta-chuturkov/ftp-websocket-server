package ftp.core.service.face;

import ftp.core.model.dto.DeletedFilesDto;
import ftp.core.model.dto.FileSharedWithUsersDto;
import ftp.core.model.dto.JsonFileDto;
import ftp.core.model.dto.PersonalFileDto;
import ftp.core.model.dto.SharedFileDto;
import ftp.core.model.dto.UploadedFilesDto;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface FileManagementService {

  String updateProfilePicture(MultipartFile file);

  UploadedFilesDto<JsonFileDto> uploadFile(MultipartFile file,
      String userNickNames);

  DeletedFilesDto deleteFiles(String deleteHash);

  FileSystemResource downloadFile(String downloadHash);

  FileSystemResource sendProfilePicture(String filename);

  String getProfilePicUrl(String userName, String serverContext);

  Page<FileSharedWithUsersDto> getFilesISharedWithOtherUsers(Pageable pageable);

  Page<PersonalFileDto> getPrivateFiles(Pageable pageable);

  Page<SharedFileDto> getFilesSharedToMe(Pageable pageable);
}
