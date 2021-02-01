package ftp.core.repository;

import ftp.core.model.dto.SharedFileDto;
import ftp.core.model.entities.File;
import ftp.core.model.entities.FileSharedToUser;
import ftp.core.model.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileSharedToUserRepository extends JpaRepository<FileSharedToUser, Long> {
    List<FileSharedToUser> findByFileId(Long fileId);

    Page<FileSharedToUser> findByUserId(Long userId, Pageable pageable);

    void deleteByFileIdAndUserId(Long fileId, Long userId);

    List<FileSharedToUser> findByUserIdAndFileId(Long userId, Long fileId);

    boolean existsByUserIdAndFileId(Long userId, Long fileId);
}
