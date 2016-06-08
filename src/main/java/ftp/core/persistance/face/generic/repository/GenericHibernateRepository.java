package ftp.core.persistance.face.generic.repository;

import com.google.common.collect.Sets;
import ftp.core.common.model.AbstractEntity;
import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Transactional
@Repository
public abstract class GenericHibernateRepository<T extends AbstractEntity, ID extends Number>
        implements GenericRepository<T, ID> {

    private final static String unchecked = "unchecked";

    @Resource
    private SessionFactory sessionFactory;

    private final Class<T> persistentClass;

    @SuppressWarnings(unchecked)
    public GenericHibernateRepository() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    public void setSessionFactory(final SessionFactory sessionFactory) {

        this.sessionFactory = sessionFactory;
    }

    protected Session getCurrentSession() {
        final Session session = this.sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public ID save(final T entity) {
        @SuppressWarnings("unchecked") final
        ID id = (ID) getCurrentSession().save(entity);
        return id;
    }

    @Override
    public void save(final Iterable<T> entities) {
        if (entities != null) {
            for (final T entity : entities) {
                save(entity);
            }
        }
    }

    protected Query getNamedQuery(final String queryName, final Object... values) {
        final Query query = getCurrentSession().getNamedQuery(queryName);
        for (int i = 0; i < values.length; i++) {
            query.setParameter(i, values[i]);
        }
        return query;
    }

    protected Query getNamedQueryWithListParameter(final String queryName, final Map<String, Set<Integer>> paramNameToValue) {
        final Query query = getCurrentSession().getNamedQuery(queryName);
        Set<Integer> paramValue = null;
        for (final String paramName : paramNameToValue.keySet()) {
            paramValue = paramNameToValue.get(paramName);
            query.setParameterList(paramName, paramValue);
        }

        return query;
    }

    @Override
    public T findOne(final ID id) {
        return id == null ? null : (T) getCurrentSession().get(this.persistentClass, id);
    }

    @SuppressWarnings(unchecked)
    @Override
    public Iterable<T> findByIds(final Collection<ID> ids) {
        if (!ids.iterator().hasNext()) {
            return Sets.newHashSet();
        }
        final Criteria c = getCurrentSession().createCriteria(this.persistentClass);
        c.add(Restrictions.in("id", ids));
        return Sets.newHashSet(c.list());
    }

    @SuppressWarnings(unchecked)
    @Override
    public T unproxy(final ID id) {
        T t = (T) getCurrentSession().load(this.persistentClass, id);
        try {
            t = (T) ((org.hibernate.proxy.HibernateProxy) t).getHibernateLazyInitializer().getImplementation();
        } catch (final Exception e) {
        }
        return t;
    }

    @Override
    public void update(final T entity) {
        getCurrentSession().update(entity);
    }

    @Override
    public void delete(final T entity) {
        getCurrentSession().delete(entity);
    }

    @Override
    public void delete(final ID id) {
        delete((T) getCurrentSession().load(this.persistentClass, id));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll() {
        return getCurrentSession().createCriteria(this.persistentClass).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();

    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<T> findAllOrderById() {
        return getCurrentSession().createCriteria(this.persistentClass).addOrder(Order.asc("id"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

    }

    @Override
    public void saveOrUpdate(final T entity) {
        getCurrentSession().saveOrUpdate(entity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T merge(final T entity) {
        return (T) getCurrentSession().merge(entity);
    }

    protected boolean isFilterEnabled(final String filterName) {
        final Filter enabledFilter = getCurrentSession().getEnabledFilter(filterName);
        return enabledFilter != null;
    }

    @Override
    public boolean exists(final ID id) {
        return id == null ? false : (getCurrentSession().get(this.persistentClass, id) == null ? false : true);
    }

    @Override
    public long count() {
        return findAll().size();
    }

}
