package ftp.core.repository;

import ftp.core.model.entities.PersistentToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA generic for the PersistentToken entity.
 */
public interface PersistentTokenRepository extends JpaRepository<PersistentToken, String> {

}
