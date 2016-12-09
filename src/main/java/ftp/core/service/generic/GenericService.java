package ftp.core.service.generic;

import ftp.core.model.entities.AbstractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

public interface GenericService<T extends AbstractEntity, ID extends Serializable> {

    Iterable<T> findAll();


    Iterable<T> findAll(Sort sort);


    Page<T> findAll(Pageable pageable);


    Iterable<T> findAll(Iterable<ID> ids);


    long count();


    void delete(ID id);


    void delete(T entity);


    void delete(Iterable<? extends T> entities);


    void deleteAll();


    <S extends T> S save(S entity);


    <S extends T> Iterable<S> save(Iterable<S> entities);


    T findOne(ID id);


    boolean exists(ID id);

}
