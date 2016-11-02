package ftp.core.repository;

import ftp.core.model.entities.User;
import ftp.core.repository.projections.NickNameProjection;
import ftp.core.repository.projections.UploadedFilesProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailAndPassword(String email, String password);

    Long findTokenByEmail(String email);

    User findByNickName(String nickName);

    User findByEmail(String email);

    List<NickNameProjection> findByNickNameLike(String nickName);

    UploadedFilesProjection findUploadedFilesById(Long id);

    Set<NickNameProjection> findByNickNameIn(Collection<String> nickNames);


}
