package ftp.core.persistance.hibernate.dao;

import ftp.core.common.model.User;
import ftp.core.persistance.face.dao.UserDao;
import ftp.core.persistance.face.generic.dao.GenericHibernateDao;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class UserHibernateDao extends GenericHibernateDao<User, Long> implements UserDao {

	@Override
	public User findByEmailAndPassword(final String email, final String password) {
		final Query query = getCurrentSession().getNamedQuery("User.findByEmailAndPassword");
		query.setParameter(0, email);
		query.setParameter(1, password);
		return (User) query.uniqueResult();
	}

	@Override
	public BigDecimal getTokenByEmail(final String email) {
		final Query query = getCurrentSession().getNamedQuery("User.getTokenByEmail");
		query.setParameter("email", email);
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getRandomTokenFromDB() {
		final SQLQuery query = getCurrentSession().createSQLQuery("select dbms_random.random from dual");
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public User getUserByNickName(final String nickName) {
		final Query query = getCurrentSession().getNamedQuery("User.getUserByNickName");
		query.setParameter("nickName", nickName);
		return (User) query.uniqueResult();
	}

	@Override
	public User getUserByEmail(final String email) {
		final Query query = getCurrentSession().getNamedQuery("User.getUserByEmail");
		query.setParameter("email", email);
		return (User) query.uniqueResult();
	}

	@Override
	public List<String> getUserByNickLike(final String userNickName) {
		final Query query = getCurrentSession()
				.createSQLQuery("select users.nickname from users where nickname like '%" + userNickName + "%'");
		return query.list();
	}

}
