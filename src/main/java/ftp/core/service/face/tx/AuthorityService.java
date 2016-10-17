package ftp.core.service.face.tx;

import ftp.core.model.entities.Authority;
import ftp.core.persistance.face.generic.service.GenericService;

import javax.transaction.Transactional;

/**
 * Created by Kosta_Chuturkov on 10/14/2016.
 */
@Transactional
public interface AuthorityService extends GenericService<Authority, Long> {
}
