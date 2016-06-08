package ftp.core.service.generic;

import ftp.core.common.model.AbstractEntity;
import ftp.core.persistance.face.generic.repository.GenericRepository;
import ftp.core.persistance.face.generic.service.GenericService;
import ftp.core.service.face.tx.FtpServerException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class AbstractGenericService<T extends AbstractEntity, ID extends Serializable>
        implements GenericService<T, ID>, ApplicationContextAware {

    private final Class<T> persistentClass;
    private ApplicationContext applicationContext;


    public AbstractGenericService() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    @Override
    public ID save(final T entity) {
        return getRepository().save(entity);
    }

    @Override
    public void save(final Iterable<T> entity) {
        getRepository().save((Iterable<T>) entity);
    }

    @Override
    public T findOne(final ID id) {
        return getRepository().findOne(id);
    }

    @Override
    public void update(final T entity) {
        getRepository().update((T) entity);
    }

    @Override
    public void delete(final T entity) {
        getRepository().delete((T) entity);
    }

    @Override
    public void delete(final ID id) {
        getRepository().delete(id);
    }

    @Override
    public Iterable<T> findAll() {
        return (Iterable<T>) getRepository().findAll();
    }

    @Override
    public void saveOrUpdate(final T entity) {
        getRepository().saveOrUpdate((T) entity);
    }

    @Override
    public T merge(final T entity) {
        return getRepository().merge((T) entity);
    }

    @Override
    public T unproxy(final ID id) {
        throw new FtpServerException("Not Supported");
    }

    @Override
    public boolean exists(final ID id) {
        return getRepository().exists(id);
    }

    @Override
    public long count() {
        return getRepository().count();
    }

    @Override
    public Iterable<T> findByIds(final Collection<ID> ids) {
        return getRepository().findByIds(ids);
    }

    @Override
    public Iterable<T> findAllOrderById() {
        return getRepository().findAllOrderById();
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private GenericRepository<T, ID> getRepository() {
        return this.applicationContext.getBean(getDaoName(this.persistentClass), GenericRepository.class);
    }

    private String getDaoName(final Class<T> entityClass) {
        final String calssName = getUnqualifiedClassName(entityClass);
        return (calssName.substring(0, 1)).toLowerCase() + calssName.substring(1) + "Repository";
    }

    private String getUnqualifiedClassName(final Class<T> entityClass) {
        return entityClass.getName().substring(entityClass.getPackage().getName().length() + 1);
    }

}
