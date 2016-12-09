package ftp.core.repository;

import ftp.core.model.entities.Authority;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by Kosta_Chuturkov on 10/14/2016.
 */
public interface AuthorityRepository extends ElasticsearchRepository<Authority, String> {
}
