package ftp.core.service.face.tx;

import ftp.core.model.entities.Authority;
import javax.transaction.Transactional;

/**
 * Created by Kosta_Chuturkov on 10/14/2016.
 */
@Transactional
public interface AuthorityService {

  Authority save(Authority authority);
}
