package ftp.core.persistance.face.generic.repository;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.stereotype.Repository;

@Repository
public interface GenericRepository<T, ID extends Serializable> {

    ID save(T entity);

    void save(Iterable<T> entity);

    T findOne(ID id);

    void update(T entity);

    void delete(T entity);

    void delete(ID id);

    Iterable<T> findAll();

    void saveOrUpdate(T entity);

    T merge(T entity);

    T unproxy(ID id);

    boolean exists(ID id);

    long count();

    Iterable<T> findByIds(Collection<ID> ids);

    Iterable<T> findAllOrderById();
}
