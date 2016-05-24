package ftp.core.persistance.face.dao;

import ftp.core.common.model.User;
import ftp.core.persistance.face.generic.dao.GenericDao;

import java.util.List;

public interface UserDao extends GenericDao<User, Long> {

    User findByEmailAndPassword(String email, String password);

    Long getTokenByEmail(String email);

    Long getRandomTokenFromDB();

    User getUserByNickName(String nickName);

    User getUserByEmail(String email);

    List<String> getUserByNickLike(String userNickName);
}
