package ftp.core.persistance.face.generic.repository;

import com.google.common.collect.Sets;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

@Transactional
@Repository
public abstract class GenericHibernateRepository<T, ID extends Serializable>
        implements GenericRepository<T, ID> {

    private final static String unchecked = "unchecked";
	private final Class<T> persistentClass;
    @Resource
    private EntityManagerFactory factory;

    @PostConstruct
    public void init(){
        this.sessionFactory = this.factory.unwrap(SessionFactory.class);
    }
    private SessionFactory sessionFactory;

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
		final
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

    @Override
    public T findOne(final ID id) {
        return id == null ? null : (T) getCurrentSession().get(this.persistentClass, id);
    }

    @Override
    public Iterable<T> findByIds(final Collection<ID> ids) {
        if (!ids.iterator().hasNext()) {
            return Sets.newHashSet();
        }
        final Criteria c = getCurrentSession().createCriteria(this.persistentClass);
        c.add(Restrictions.in("id", ids));
        return Sets.newHashSet(c.list());
    }

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

    @Override
    public List<T> findAll() {
        return getCurrentSession().createCriteria(this.persistentClass).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();

    }

    @Override
    public Iterable<T> findAllOrderById() {
        return getCurrentSession().createCriteria(this.persistentClass).addOrder(Order.asc("id"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

    }

    @Override
    public void saveOrUpdate(final T entity) {
        getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public T merge(final T entity) {
        return (T) getCurrentSession().merge(entity);
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
