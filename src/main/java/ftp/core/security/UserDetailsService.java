package ftp.core.security;

import ftp.core.model.entities.User;
import ftp.core.repository.UserRepository;
import ftp.core.service.face.tx.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Locale;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    @Resource
    private UserRepository userRepository;

    @Resource
    private UserService userService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        this.log.debug("Authenticating {}", login);
        final String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        final User userFromDatabase = this.userRepository.getUserByEmail(lowercaseLogin);
        if (userFromDatabase != null) {
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = context.getAuthentication();
            //authentication.getPrincipal()
            return userFromDatabase;
        } else {
            throw new UsernameNotFoundException("Invalid credentials");
        }

    }

}
