package ftp.core.persistance.face.generic.service;

import ftp.core.model.entities.AbstractEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

public interface GenericService<T extends AbstractEntity, ID extends Serializable> {

    List<T> findAll();


    List<T> findAll(Sort sort);


    Page<T> findAll(Pageable pageable);


    List<T> findAll(Iterable<ID> ids);


    long count();


    void delete(ID id);


    void delete(T entity);


    void delete(Iterable<? extends T> entities);


    void deleteAll();


    <S extends T> S save(S entity);


    <S extends T> List<S> save(Iterable<S> entities);


    T findOne(ID id);


    boolean exists(ID id);


    void flush();


    <S extends T> S saveAndFlush(S entity);


    void deleteInBatch(Iterable<T> entities);


    void deleteAllInBatch();


    T getOne(ID id);


    <S extends T> S findOne(Example<S> example);


    <S extends T> List<S> findAll(Example<S> example);


    <S extends T> List<S> findAll(Example<S> example, Sort sort);


    <S extends T> Page<S> findAll(Example<S> example, Pageable pageable);


    <S extends T> long count(Example<S> example);


    <S extends T> boolean exists(Example<S> example);

}
