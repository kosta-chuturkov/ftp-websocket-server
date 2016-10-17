package ftp.core.service.impl;

import ftp.core.model.entities.Authority;
import ftp.core.service.face.tx.AuthorityService;
import ftp.core.service.generic.AbstractGenericService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by Kosta_Chuturkov on 10/14/2016.
 */
@Service("authorityService")
@Transactional
public class AuthorityServiceImpl extends AbstractGenericService<Authority, Long> implements AuthorityService {
}
