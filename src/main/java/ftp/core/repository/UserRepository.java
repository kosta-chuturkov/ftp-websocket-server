package ftp.core.repository;

import ftp.core.model.entities.User;
import ftp.core.repository.generic.GenericRepository;

import java.util.List;

public interface UserRepository extends GenericRepository<User, Long> {

    User findByEmailAndPassword(String email, String password);

    Long getTokenByEmail(String email);

    Long getRandomTokenFromDB();

    User getUserByNickName(String nickName);

    User getUserByEmail(String email);

    List<String> getUserByNickLike(String userNickName);
}
