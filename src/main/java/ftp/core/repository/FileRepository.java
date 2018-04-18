package ftp.core.repository;

import ftp.core.model.entities.File;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileRepository extends JpaRepository<File, Long> {

  File findByDownloadHash(String downloadHash);

  File findByDeleteHashAndCreatorNickName(String deleteHash, String creatorNickName);

  @Query("select file " +
      " from File file" +
      "               left outer join fetch file.sharedWithUsers swu" +
      "               where :userNickName in(swu)" +
      "               and file.fileType = :fileType")
  List<File> findSharedFilesWithMe(@Param("userNickName") String userNickName,
      @Param("fileType") File.FileType fileType, Pageable pageable);

  List<File> findByCreatorNickNameAndFileType(String creatorNickName, File.FileType fileType,
      Pageable pageable);

}
