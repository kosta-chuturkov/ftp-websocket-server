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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Transactional
public interface FileManagementService {

    UploadedFilesDto<JsonFileDto> uploadFile(MultipartFile file,
                                             String userNickNames);

    DeletedFilesDto deleteFiles(String deleteHash);

    void downloadFile(String downloadHash, HttpServletResponse response);

    Page<FileSharedWithUsersDto> getFilesISharedWithOtherUsers(Pageable pageable);

    Page<PersonalFileDto> getPrivateFiles(Pageable pageable);

    Page<SharedFileDto> getFilesSharedToMe(Pageable pageable);

    File updateFile(FileUpdateRequest updateRequest);

    FileSharedToUser uploadMockupData();

    Page<File> getAllFiles(Pageable pageable, String fileType);

    Page<File> findByQuery(String query, String fileType, Pageable pageable);

    void copy(InputStream source, OutputStream target) throws IOException;
}
