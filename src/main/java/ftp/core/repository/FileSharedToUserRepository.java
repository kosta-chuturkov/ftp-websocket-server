package ftp.core.repository;

import ftp.core.model.entities.File;
import ftp.core.model.entities.FileSharedToUser;
import ftp.core.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileSharedToUserRepository extends JpaRepository<FileSharedToUser, Long> {
    List<FileSharedToUser> findByFile(File file);

    List<FileSharedToUser> findByUser(User user);
}
