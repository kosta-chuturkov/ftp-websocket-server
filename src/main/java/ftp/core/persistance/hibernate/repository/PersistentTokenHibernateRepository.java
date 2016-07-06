package ftp.core.persistance.hibernate.repository;

import org.springframework.stereotype.Component;

import ftp.core.common.model.PersistentToken;
import ftp.core.persistance.face.generic.repository.GenericHibernateRepository;
import ftp.core.persistance.face.repository.PersistentTokenRepository;

/**
 * Created by Kosta_Chuturkov on 7/6/2016.
 */
@Component
public class PersistentTokenHibernateRepository extends GenericHibernateRepository<PersistentToken, String>
		implements PersistentTokenRepository {
}
