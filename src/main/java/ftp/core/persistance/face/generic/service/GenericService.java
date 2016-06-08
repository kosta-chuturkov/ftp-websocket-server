package ftp.core.persistance.face.generic.service;

import ftp.core.common.model.AbstractEntity;
import ftp.core.persistance.face.generic.repository.GenericRepository;

import java.io.Serializable;

public interface GenericService<T extends AbstractEntity, ID extends Serializable> extends GenericRepository<T, ID> {

}
