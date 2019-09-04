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
    List<FileSharedToUser> findByFile(File file);

    Page<SharedFileDto> findByUser(User user, Pageable pageable);

    void deleteByFileAndUser(File file, User user);

    List<FileSharedToUser> findByUserAndFile(User user, File file);

    boolean existsByUserAndFile(User user, File file);
}
