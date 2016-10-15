package ftp.core.persistance.hibernate.repository;

import ftp.core.model.entities.PersistentToken;
import ftp.core.persistance.face.generic.repository.GenericHibernateRepository;
import ftp.core.persistance.face.repository.PersistentTokenRepository;
import org.springframework.stereotype.Component;

/**
 * Created by Kosta_Chuturkov on 7/6/2016.
 */
@Component
public class PersistentTokenHibernateRepository extends GenericHibernateRepository<PersistentToken, String>
        implements PersistentTokenRepository {
}
