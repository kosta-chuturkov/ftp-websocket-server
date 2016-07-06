package ftp.core.security;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ftp.core.common.model.User;
import ftp.core.persistance.face.repository.UserRepository;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

	@Resource
	private UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String login) {
		this.log.debug("Authenticating {}", login);
		final String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
		final User userFromDatabase = this.userRepository.getUserByEmail(lowercaseLogin);
		if (userFromDatabase != null) {
			final List<GrantedAuthority> grantedAuthorities = userFromDatabase.getAuthorities().stream()
					.map(authority -> new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());
			return new org.springframework.security.core.userdetails.User(lowercaseLogin,
					userFromDatabase.getPassword(), grantedAuthorities);
		} else {
			throw new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the " + "database");
		}

	}
}
