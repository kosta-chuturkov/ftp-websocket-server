package ftp.core.persistance.face.generic.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import ftp.core.common.model.AbstractEntity;

@Transactional
@Repository
public abstract class GenericHibernateDao<T extends AbstractEntity, ID extends Number>
		implements GenericDao<T, ID> {

	private final static String unchecked = "unchecked";

	@Resource
	private SessionFactory sessionFactory;

	private final Class<T> persistentClass;

	@SuppressWarnings(unchecked)
	public GenericHibernateDao() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}

	public void setSessionFactory(SessionFactory sessionFactory) {

		this.sessionFactory = sessionFactory;
	}

	protected Session getCurrentSession() {
		Session session = sessionFactory.getCurrentSession();
		return session;
	}

	@Override
	public ID save(T entity) {
		@SuppressWarnings("unchecked")
		ID id = (ID) getCurrentSession().save(entity);
		return id;
	}

	@Override
	public void save(Iterable<T> entities) {
		if (entities != null) {
			for (T entity : entities) {
				save(entity);
			}
		}
	}

	protected Query getNamedQuery(String queryName, Object... values) {
		Query query = getCurrentSession().getNamedQuery(queryName);
		for (int i = 0; i < values.length; i++) {
			query.setParameter(i, values[i]);
		}
		return query;
	}

	protected Query getNamedQueryWithListParameter(String queryName, Map<String, Set<Integer>> paramNameToValue) {
		Query query = getCurrentSession().getNamedQuery(queryName);
		Set<Integer> paramValue = null;
		for (String paramName : paramNameToValue.keySet()) {
			paramValue = paramNameToValue.get(paramName);
			query.setParameterList(paramName, paramValue);
		}

		return query;
	}

	@Override
	public T findOne(ID id) {
		return id == null ? null : (T) getCurrentSession().get(persistentClass, id);
	}

	@SuppressWarnings(unchecked)
	@Override
	public Iterable<T> findByIds(Collection<ID> ids) {
		if (!ids.iterator().hasNext()) {
			return Sets.newHashSet();
		}
		Criteria c = getCurrentSession().createCriteria(persistentClass);
		c.add(Restrictions.in("id", ids));
		return Sets.newHashSet(c.list());
	}

	@SuppressWarnings(unchecked)
	@Override
	public T unproxy(ID id) {
		T t = (T) getCurrentSession().load(persistentClass, id);
		try {
			t = (T) ((org.hibernate.proxy.HibernateProxy) t).getHibernateLazyInitializer().getImplementation();
		} catch (Exception e) {
		}
		return t;
	}

	@Override
	public void update(T entity) {
		getCurrentSession().update(entity);
	}

	@Override
	public void delete(T entity) {
		getCurrentSession().delete(entity);
	}

	@Override
	public void delete(ID id) {
		delete((T) getCurrentSession().load(persistentClass, id));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAll() {
		return getCurrentSession().createCriteria(persistentClass).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();

	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<T> findAllOrderById() {
		return getCurrentSession().createCriteria(persistentClass).addOrder(Order.asc("id"))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

	}

	@Override
	public void saveOrUpdate(T entity) {
		getCurrentSession().saveOrUpdate(entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T merge(T entity) {
		return (T) getCurrentSession().merge(entity);
	}

	protected boolean isFilterEnabled(String filterName) {
		Filter enabledFilter = getCurrentSession().getEnabledFilter(filterName);
		return enabledFilter != null;
	}

	@Override
	public boolean exists(ID id) {
		return id == null ? false : (getCurrentSession().get(persistentClass, id) == null ? false : true);
	}

	@Override
	public long count() {
		return findAll().size();
	}

}
