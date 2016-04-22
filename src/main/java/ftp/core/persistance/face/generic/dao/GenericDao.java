package ftp.core.persistance.face.generic.dao;

import ftp.core.common.model.AbstractEntity;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Collection;

@Repository
public interface GenericDao<T extends AbstractEntity, ID extends Serializable> {

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
