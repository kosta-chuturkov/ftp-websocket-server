package ftp.core.service.face;

import ftp.core.model.dto.DeletedFilesDto;
import ftp.core.model.dto.FileSharedWithUsersDto;
import ftp.core.model.dto.FileUpdateRequest;
import ftp.core.model.dto.JsonFileDto;
import ftp.core.model.dto.PersonalFileDto;
import ftp.core.model.dto.SharedFileDto;
import ftp.core.model.dto.UploadedFilesDto;
import ftp.core.model.entities.File;
import ftp.core.model.entities.FileSharedToUser;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Transactional
public interface FileManagementService {

    UploadedFilesDto<JsonFileDto> uploadFile(MultipartFile file,
                                             String userNickNames);

    DeletedFilesDto deleteFiles(String deleteHash);

    FileSystemResource downloadFile(String downloadHash);

    Page<FileSharedWithUsersDto> getFilesISharedWithOtherUsers(Pageable pageable);

    Page<PersonalFileDto> getPrivateFiles(Pageable pageable);

    Page<SharedFileDto> getFilesSharedToMe(Pageable pageable);

    File updateFile(FileUpdateRequest updateRequest);

    FileSharedToUser test();

    Page<File> getAllFiles(Pageable pageable);

    Page<File> findByQuery(String query, Pageable pageable);
}
