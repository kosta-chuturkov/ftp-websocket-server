package ftp.core.persistance.face.generic.service;

import ftp.core.common.model.AbstractEntity;
import ftp.core.persistance.face.generic.dao.GenericDao;

public interface GenericService<T extends AbstractEntity, ID> extends GenericDao<T, ID> {

}
