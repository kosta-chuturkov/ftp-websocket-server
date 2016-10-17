package ftp.core.service.generic;

import ftp.core.model.entities.AbstractEntity;
import ftp.core.persistance.face.generic.repository.GenericRepository;
import ftp.core.persistance.face.generic.service.GenericService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class AbstractGenericService<T extends AbstractEntity, ID extends Serializable>
        implements GenericService<T, ID>, ApplicationContextAware {

    private final Class<T> persistentClass;
    private ApplicationContext applicationContext;


    public AbstractGenericService() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
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

    @Override
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return getRepository().findAll(sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    @Override
    public List<T> findAll(Iterable<ID> ids) {
        return getRepository().findAll(ids);
    }

    @Override
    public long count() {
        return getRepository().count();
    }

    @Override
    public void delete(ID id) {
        getRepository().delete(id);
    }

    @Override
    public void delete(T entity) {
        getRepository().delete(entity);
    }

    @Override
    public void delete(Iterable<? extends T> entities) {
        getRepository().delete(entities);
    }

    @Override
    public void deleteAll() {
        getRepository().deleteAll();
    }

    @Override
    public <S extends T> S save(S entity) {
        return getRepository().save(entity);
    }

    @Override
    public <S extends T> List<S> save(Iterable<S> entities) {
        return getRepository().save(entities);
    }

    @Override
    public T findOne(ID id) {
        return getRepository().findOne(id);
    }

    @Override
    public boolean exists(ID id) {
        return getRepository().exists(id);
    }

    @Override
    public void flush() {
        getRepository().flush();
    }

    @Override
    public <S extends T> S saveAndFlush(S entity) {
        return getRepository().saveAndFlush(entity);
    }

    @Override
    public void deleteInBatch(Iterable<T> entities) {
        getRepository().deleteInBatch(entities);
    }

    @Override
    public void deleteAllInBatch() {
        getRepository().deleteAllInBatch();
    }

    @Override
    public T getOne(ID id) {
        return getRepository().getOne(id);
    }

    @Override
    public <S extends T> S findOne(Example<S> example) {
        return getRepository().findOne(example);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return getRepository().findAll(example);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        return getRepository().findAll(example, sort);
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return getRepository().findAll(example, pageable);
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        return getRepository().count(example);
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return getRepository().exists(example);
    }
}
