package ftp.core.persistance.face.generic.service;

import java.io.Serializable;

import ftp.core.common.model.AbstractEntity;
import ftp.core.persistance.face.generic.dao.GenericDao;

public interface GenericService<T extends AbstractEntity, ID extends Serializable> extends GenericDao<AbstractEntity, Number>{

}
