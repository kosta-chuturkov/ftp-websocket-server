package ftp.core.repository;

import ftp.core.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailAndPassword(String email, String password);

    Long findTokenByEmail(String email);

    User findByNickName(String nickName);

    User findByEmail(String email);

    List<String> findByNickNameLike(String userNickName);
}
