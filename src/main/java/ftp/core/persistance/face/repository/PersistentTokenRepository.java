package ftp.core.persistance.face.repository;

import ftp.core.model.entities.PersistentToken;
import ftp.core.persistance.face.generic.repository.GenericRepository;

/**
 * Spring Data JPA repository for the PersistentToken entity.
 */
public interface PersistentTokenRepository extends GenericRepository<PersistentToken, String> {

}
