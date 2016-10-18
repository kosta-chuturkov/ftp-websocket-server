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
            "               and file.fileType = ftp.core.model.entities.File$FileType.SHARED")
    List<File> findAllSharedFilesByUserNickName(@Param("userNickName") String userNickName, Pageable pageable);

    @Query("select file " +
            "   from File file" +
            "               left outer join fetch file.sharedWithUsers swu" +
            "               where :userNickName in(swu)" +
            "               and file.fileType = ftp.core.model.entities.File$FileType.PRIVATE")
    List<File> findAllPrivateFilesByUserNickName(@Param("userNickName") String userNickName, Pageable pageable);

    @Query("select fls.id " +
            "    from User usr" +
            "    left outer join usr.uploadedFiles fls" +
            "    where fls.fileType = ftp.core.model.entities.File$FileType.SHARED" +
            "    and usr.id=:userId")
    List<Long> findSharedFilesIdsByUserId(@Param("userId") Long userId, Pageable pageable);

}
