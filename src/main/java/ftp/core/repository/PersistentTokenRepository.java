package ftp.core.repository;

import ftp.core.model.entities.PersistentToken;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data JPA generic for the PersistentToken entity.
 */
public interface PersistentTokenRepository extends ElasticsearchRepository<PersistentToken, String> {

}
