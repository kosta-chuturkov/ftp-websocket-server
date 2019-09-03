package ftp.core.repository;

import ftp.core.model.dto.FileSharedWithUsersDto;
import ftp.core.model.dto.PersonalFileDto;
import ftp.core.model.dto.SharedFileDto;
import ftp.core.model.entities.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

  File findByDownloadHash(String downloadHash);

  List<File> findByCreator_Id(Long creatorId);

  File findByDeleteHashAndCreatorNickName(String deleteHash, String creatorNickName);

  @Query("select file " +
      "from File file" +
      " where :userNickName in(file.sharedWithUsers)" +
      "and file.fileType = :fileType")
  Page<SharedFileDto> findSharedFilesWithMe(@Param("userNickName") String userNickName,
      @Param("fileType") File.FileType fileType, Pageable pageable);

  Page<PersonalFileDto> findByCreatorNickNameAndFileType(String creatorNickName, File.FileType fileType, Pageable pageable);

  Page<FileSharedWithUsersDto> findByFileTypeAndCreatorNickName(File.FileType fileType, String creatorNickName, Pageable pageable);

}
