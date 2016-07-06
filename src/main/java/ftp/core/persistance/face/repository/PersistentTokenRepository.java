package ftp.core.persistance.face.repository;

import ftp.core.common.model.PersistentToken;
import ftp.core.persistance.face.generic.repository.GenericRepository;

/**
 * Spring Data JPA repository for the PersistentToken entity.
 */
public interface PersistentTokenRepository extends GenericRepository<PersistentToken, String> {

}
