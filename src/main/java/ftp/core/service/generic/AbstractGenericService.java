package ftp.core.service.generic;

import ftp.core.model.entities.AbstractEntity;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

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

    protected ElasticsearchRepository<T, ID> getRepository() {
        return this.applicationContext.getBean(getDaoName(this.persistentClass), ElasticsearchRepository.class);
    }

    private String getDaoName(final Class<T> entityClass) {
        final String calssName = getUnqualifiedClassName(entityClass);
        return (calssName.substring(0, 1)).toLowerCase() + calssName.substring(1) + "Repository";
    }

    private String getUnqualifiedClassName(final Class<T> entityClass) {
        return entityClass.getName().substring(entityClass.getPackage().getName().length() + 1);
    }

    @Override
    public Iterable<T> findAll() {
        return getRepository().findAll();
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        return getRepository().findAll(sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    @Override
    public Iterable<T> findAll(Iterable<ID> ids) {
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
    public <S extends T> Iterable<S> save(Iterable<S> entities) {
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

}
