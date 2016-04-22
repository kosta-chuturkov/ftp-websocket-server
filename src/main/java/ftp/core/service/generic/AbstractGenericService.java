package ftp.core.service.generic;

import ftp.core.common.model.AbstractEntity;
import ftp.core.persistance.face.generic.dao.GenericDao;
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
		return getDao().save(entity);
	}

	@Override
	public void save(final Iterable<T> entity) {
		getDao().save((Iterable<T>) entity);
	}

	@Override
	public T findOne(final ID id) {
		return getDao().findOne(id);
	}

	@Override
	public void update(final T entity) {
		getDao().update((T) entity);
	}

	@Override
	public void delete(final T entity) {
		getDao().delete((T) entity);
	}

	@Override
	public void delete(final ID id) {
		getDao().delete(id);
	}

	@Override
	public Iterable<T> findAll() {
		return (Iterable<T>) getDao().findAll();
	}

	@Override
	public void saveOrUpdate(final T entity) {
		getDao().saveOrUpdate((T) entity);
	}

	@Override
	public T merge(final T entity) {
		return getDao().merge((T) entity);
	}

	@Override
	public T unproxy(final ID id) {
		throw new FtpServerException("Not Supported");
	}

	@Override
	public boolean exists(final ID id) {
		return getDao().exists(id);
	}

	@Override
	public long count() {
		return getDao().count();
	}

	@Override
	public Iterable<T> findByIds(final Collection<ID> ids) {
		return getDao().findByIds(ids);
	}

	@Override
	public Iterable<T> findAllOrderById() {
		return getDao().findAllOrderById();
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	private GenericDao<T, ID> getDao() {
		return this.applicationContext.getBean(getDaoName(this.persistentClass), GenericDao.class);
	}

	private String getDaoName(final Class<T> entityClass) {
		final String calssName = getUnqualifiedClassName(entityClass);
		return (calssName.substring(0, 1)).toLowerCase() + calssName.substring(1) + "Dao";
	}

	private String getUnqualifiedClassName(final Class<T> entityClass) {
		return entityClass.getName().substring(entityClass.getPackage().getName().length() + 1);
	}

}
