package ftp.core.persistance.hibernate.repository;

import ftp.core.model.entities.Authority;
import ftp.core.persistance.face.generic.repository.GenericHibernateRepository;
import ftp.core.persistance.face.repository.AuthorityRepository;
import org.springframework.stereotype.Component;

/**
 * Created by Kosta_Chuturkov on 10/14/2016.
 */
@Component
public class AuthorityHibernateRepository extends GenericHibernateRepository<Authority, Long> implements AuthorityRepository {
}
