package ftp.core.service.generic;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ftp.core.common.model.AbstractEntity;
import ftp.core.persistance.face.generic.dao.GenericDao;
import ftp.core.persistance.face.generic.service.GenericService;
import ftp.core.service.face.tx.FtpServerException;

public class AbstractGenericService<T extends AbstractEntity, ID extends Number>
		implements GenericService<AbstractEntity, Number>, ApplicationContextAware {

	private ApplicationContext applicationContext;
	private final Class<T> persistentClass;


	@SuppressWarnings("unchecked")
	public AbstractGenericService() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}

	@Override
	public ID save(AbstractEntity entity) {		
		return getDao().save((T) entity);
	}

	@Override
	public void save(Iterable<AbstractEntity> entity) {
		getDao().save((Iterable<T>) entity);
	}

	@Override
	public AbstractEntity findOne(Number id) {
		return getDao().findOne((ID) id);
	}

	@Override
	public void update(AbstractEntity entity) {
		getDao().update((T) entity);
	}

	@Override
	public void delete(AbstractEntity entity) {
		getDao().delete((T) entity);
	}

	@Override
	public void delete(Number id) {
		getDao().delete((ID) id);
	}

	@Override
	public Iterable<AbstractEntity> findAll() {
		return (Iterable<AbstractEntity>) getDao().findAll();
	}

	@Override
	public void saveOrUpdate(AbstractEntity entity) {
		getDao().saveOrUpdate((T) entity);
	}

	@Override
	public AbstractEntity merge(AbstractEntity entity) {
		return getDao().merge((T) entity);
	}

	@Override
	public AbstractEntity unproxy(Number id) {
		throw new FtpServerException("Not Supported");
	}

	@Override
	public boolean exists(Number id) {
		return getDao().exists((ID) id);
	}

	@Override
	public long count() {
		return getDao().count();
	}

	@Override
	public Iterable<AbstractEntity> findByIds(Collection<Number> ids) {
		return (Iterable<AbstractEntity>) getDao().findByIds((Collection<ID>) ids);
	}

	@Override
	public Iterable<AbstractEntity> findAllOrderById() {
		return (Iterable<AbstractEntity>) getDao().findAllOrderById();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	@SuppressWarnings("unchecked")
	private GenericDao<T, ID> getDao() {
		return applicationContext.getBean(getDaoName(persistentClass), GenericDao.class);
	}
	
	private String getDaoName(Class<T> entityClass) {
		String calssName = getUnqualifiedClassName(entityClass);
		return (calssName.substring(0, 1)).toLowerCase() + calssName.substring(1) + "Dao";
	}

	private String getUnqualifiedClassName(Class<T> entityClass) {
		return entityClass.getName().substring(entityClass.getPackage().getName().length() + 1);
	}

}
