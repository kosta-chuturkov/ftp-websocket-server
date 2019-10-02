package ftp.core.security;

import ftp.core.model.entities.User;
import ftp.core.repository.UserRepository;
import ftp.core.service.face.tx.UserService;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserDetailsService implements
        org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    private UserRepository userRepository;

    @Autowired
    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        this.log.debug("Authenticating {}", login);
        final String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        final User userFromDatabase = this.userRepository.findByEmail(lowercaseLogin);
        if (userFromDatabase != null) {
            return userFromDatabase;
        } else {
            throw new UsernameNotFoundException("Invalid credentials");
        }

    }

}
