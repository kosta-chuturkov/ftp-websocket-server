package ftp.core.repository;

import ftp.core.model.entities.PersistentToken;
import ftp.core.repository.generic.GenericRepository;

/**
 * Spring Data JPA generic for the PersistentToken entity.
 */
public interface PersistentTokenRepository extends GenericRepository<PersistentToken, String> {

}
