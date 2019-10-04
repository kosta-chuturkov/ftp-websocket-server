//package ftp.core.service.impl;
//
//import ftp.core.model.entities.Authority;
//import ftp.core.repository.AuthorityRepository;
//import ftp.core.service.face.tx.AuthorityService;
//
//import javax.transaction.Transactional;
//
//import org.springframework.stereotype.Service;
//
///**
// * Created by Kosta_Chuturkov on 10/14/2016.
// */
//@Service("authorityService")
//@Transactional
//public class AuthorityServiceImpl implements
//        AuthorityService {
//
//    private final AuthorityRepository authorityRepository;
//
//    public AuthorityServiceImpl(AuthorityRepository authorityRepository) {
//        this.authorityRepository = authorityRepository;
//    }
//
//    @Override
//    public Authority save(Authority authority) {
//        return authorityRepository.save(authority);
//    }
//}
