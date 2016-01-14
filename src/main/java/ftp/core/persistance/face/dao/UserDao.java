package ftp.core.persistance.face.dao;

import java.math.BigDecimal;
import java.util.List;

import ftp.core.common.model.User;
import ftp.core.persistance.face.generic.dao.GenericDao;

public interface UserDao extends GenericDao<User, Long>{

	User findByEmailAndPassword(String email, String password);
	
	BigDecimal getTokenByEmail(String email);
	
	BigDecimal getRandomTokenFromDB();
	
	User getUserByNickName(String nickName);
	
	User getUserByEmail(String email);
	
	List<String> getUserByNickLike(String userNickName);
}
