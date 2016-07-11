package ftp.core.security;

import ftp.core.common.model.User;
import ftp.core.service.face.tx.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Kosta_Chuturkov on 7/11/2016.
 */
public class CusomDaoAuthenticationProvider extends DaoAuthenticationProvider {

	private final UserService userService;

	public CusomDaoAuthenticationProvider(final UserService userService) {
		super();

		this.userService = userService;
	}

	@Override
	protected void additionalAuthenticationChecks(final UserDetails userDetails,
			final UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		final Object salt = null;

		if (authentication.getCredentials() == null) {
			this.logger.debug("Authentication failed: no credentials provided");

			throw new BadCredentialsException(this.messages
					.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}

		final Long tokenByEmail = User.getCurrent().getToken();
		final String presentedPassword = this.userService
				.getUserSaltedPassword(authentication.getCredentials().toString(), tokenByEmail);

		if (!super.getPasswordEncoder().isPasswordValid(userDetails.getPassword(), presentedPassword, salt)) {
			this.logger.debug("Authentication failed: password does not match stored value");

			throw new BadCredentialsException(this.messages
					.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}
	}
}
