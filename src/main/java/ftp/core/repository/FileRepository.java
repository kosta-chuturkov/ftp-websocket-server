package ftp.core.repository;

import ftp.core.model.entities.File;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    File findByDownloadHash(String downloadHash);

    void deleteByDeleteHashAndCreatorNickName(String deleteHash, String creatorNickName);

    File findByDeleteHashAndCreatorNickName(String deleteHash, String creatorNickName);

    @Query("select file " +
            " from File file" +
            "               left outer join fetch file.sharedWithUsers swu" +
            "               where :userNickName in(swu)" +
            "               and file.fileType = :fileType")
    List<File> findAllSharedFilesByUserNickNameAndFileType(@Param("userNickName") String userNickName, @Param("fileType") File.FileType fileType, Pageable pageable);

    @Query("select file " +
            "   from File file" +
            "               left outer join fetch file.sharedWithUsers swu" +
            "               where :userNickName in(swu)" +
            "               and file.fileType = :fileType")
    List<File> findAllPrivateFilesByUserNickNameAndFileType(@Param("userNickName") String userNickName, @Param("fileType") File.FileType fileType, Pageable pageable);

    @Query("select fls.id " +
            "    from User usr" +
            "    left outer join usr.uploadedFiles fls" +
            "    where fls.fileType = :fileType" +
            "    and usr.id=:userId")
    List<Long> findSharedFilesIdsByUserIdAndFileType(@Param("userId") Long userId, @Param("fileType") File.FileType fileType, Pageable pageable);

}
