package ftp.core.security;

import ftp.core.constants.ServerConstants;
import ftp.core.model.entities.PersistentToken;
import ftp.core.model.entities.User;
import ftp.core.repository.PersistentTokenRepository;
import ftp.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Custom implementation of Spring Security's RememberMeServices.
 * <p>
 * Persistent tokens are used by Spring Security to automatically log in users.
 * <p>
 * This is a specific implementation of Spring Security's remember-me
 * authentication, but it is much more powerful than the standard
 * implementations:
 * <ul>
 * <li>It allows a user to see the list of his currently opened sessions, and
 * invalidate them</li>
 * <li>It stores more information, such as the IP address and the user agent,
 * for audit purposes
 * <li>
 * <li>When a user logs out, only his current session is invalidated, and not
 * all of his sessions</li>
 * </ul>
 * <p>
 * This is inspired by:
 * <ul>
 * <li><a href=
 * "http://jaspan.com/improved_persistent_login_cookie_best_practice">Improved
 * Persistent Login Cookie Best Practice</a></li>
 * <li><a href="https://github.com/blog/1661-modeling-your-app-s-user-session">
 * Github's "Modeling your App's User Session"</a></li>
 * </ul>
 * <p>
 * The main algorithm comes from Spring Security's
 * PersistentTokenBasedRememberMeServices, but this class couldn't be cleanly
 * extended.
 */
@Service
public class CustomPersistentRememberMeServices extends AbstractRememberMeServices {

    // Token is valid for one month
    private static final int TOKEN_VALIDITY_DAYS = 31;
    private static final int TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * TOKEN_VALIDITY_DAYS;
    private static final int DEFAULT_SERIES_LENGTH = 16;
    private static final int DEFAULT_TOKEN_LENGTH = 16;
    private final Logger log = LoggerFactory.getLogger(CustomPersistentRememberMeServices.class);
    private final SecureRandom random;

    @Resource
    private PersistentTokenRepository persistentTokenRepository;

    @Resource
    private UserRepository userRepository;

    @Autowired
    public CustomPersistentRememberMeServices(
            final org.springframework.security.core.userdetails.UserDetailsService userDetailsService) {

        super(ServerConstants.REMEMBER_ME_SECURITY_KEY, userDetailsService);
        this.random = new SecureRandom();
    }

    @Override
    protected UserDetails processAutoLoginCookie(final String[] cookieTokens, final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        final PersistentToken token = getPersistentToken(cookieTokens);
        final String login = token.getUser().getEmail();

        // Token also matches, so login is valid. Update the token value,
        // keeping the *same* series number.
        this.log.debug("Refreshing persistent login token for user '{}', series '{}'", login, token.getSeries());
        token.setTokenDate(LocalDate.now());
        token.setTokenValue(generateTokenData());
        token.setIpAddress(request.getRemoteAddr());
        token.setUserAgent(request.getHeader("User-Agent"));
        try {
            this.persistentTokenRepository.save(token);
            addCookie(token, request, response);
        } catch (final DataAccessException e) {
            this.log.error("Failed to update token: ", e);
            throw new RememberMeAuthenticationException("Autologin failed due to data access problem", e);
        }
        return getUserDetailsService().loadUserByUsername(login);
    }

    @Override
    protected void onLoginSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                  final Authentication successfulAuthentication) {

        final String login = successfulAuthentication.getName();
        this.log.debug("Creating new persistent login for user {}", login);
        final User user = this.userRepository.getUserByEmail(login);
        if (user != null) {
            final PersistentToken persistentToken = new PersistentToken();
            persistentToken.setSeries(generateSeriesData());
            persistentToken.setUser(user);
            persistentToken.setTokenValue(generateTokenData());
            persistentToken.setTokenDate(LocalDate.now());
            persistentToken.setIpAddress(request.getRemoteAddr());
            persistentToken.setUserAgent(request.getHeader("User-Agent"));
            try {
                this.persistentTokenRepository.save(persistentToken);
                addCookie(persistentToken, request, response);
            } catch (final DataAccessException e) {
                this.log.error("Failed to save persistent token ", e);
            }
        } else {
            throw new UsernameNotFoundException("User " + login + " was not found in the database");
        }
    }

    /**
     * When logout occurs, only invalidate the current token, and not all user
     * sessions.
     * <p>
     * The standard Spring Security implementations are too basic: they
     * invalidate all tokens for the current user, so when he logs out from one
     * browser, all his other sessions are destroyed.
     */
    @Override
    @Transactional
    public void logout(final HttpServletRequest request, final HttpServletResponse response,
                       final Authentication authentication) {
        final String rememberMeCookie = extractRememberMeCookie(request);
        if (rememberMeCookie != null && rememberMeCookie.length() != 0) {
            try {
                final String[] cookieTokens = decodeCookie(rememberMeCookie);
                final PersistentToken token = getPersistentToken(cookieTokens);
                this.persistentTokenRepository.delete(token);
            } catch (final InvalidCookieException ice) {
                this.log.info("Invalid cookie, no persistent token could be deleted");
            } catch (final RememberMeAuthenticationException rmae) {
                this.log.debug("No persistent token found, so no token could be deleted");
            }
        }
        super.logout(request, response, authentication);
    }

    /**
     * Validate the token and return it.
     */
    private PersistentToken getPersistentToken(final String[] cookieTokens) {
        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2 + " tokens, but contained '"
                    + Arrays.asList(cookieTokens) + "'");
        }
        final String presentedSeries = cookieTokens[0];
        final String presentedToken = cookieTokens[1];
        final PersistentToken token = this.persistentTokenRepository.findOne(presentedSeries);

        if (token == null) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }

        // We have a match for this user/series combination
        this.log.info("presentedToken={} / tokenValue={}", presentedToken, token.getTokenValue());
        if (!presentedToken.equals(token.getTokenValue())) {
            // Token doesn't match series value. Delete this session and throw
            // an exception.
            this.persistentTokenRepository.delete(token);
            throw new CookieTheftException(
                    "Invalid remember-me token (Series/token) mismatch. Implies previous " + "cookie theft attack.");
        }

        if (token.getTokenDate().plusDays(TOKEN_VALIDITY_DAYS).isBefore(LocalDate.now())) {
            this.persistentTokenRepository.delete(token);
            throw new RememberMeAuthenticationException("Remember-me login has expired");
        }
        return token;
    }

    private String generateSeriesData() {
        final byte[] newSeries = new byte[DEFAULT_SERIES_LENGTH];
        this.random.nextBytes(newSeries);
        return new String(Base64.encode(newSeries));
    }

    private String generateTokenData() {
        final byte[] newToken = new byte[DEFAULT_TOKEN_LENGTH];
        this.random.nextBytes(newToken);
        return new String(Base64.encode(newToken));
    }

    private void addCookie(final PersistentToken token, final HttpServletRequest request,
                           final HttpServletResponse response) {
        setCookie(new String[]{token.getSeries(), token.getTokenValue()}, TOKEN_VALIDITY_SECONDS, request, response);
    }
}
