package ftp.core.security;

import static ftp.core.security.Authorities.USER;
import static org.assertj.core.api.Assertions.assertThat;

import ftp.core.model.entities.Authority;
import ftp.core.model.entities.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Test class for the SecurityUtils utility class.
 *
 * @see
 */
public class SecurityUtilsUnitTest {

  @Test
  public void testIsAuthenticated() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("admin", "admin", Collections.singletonList(new Authority(USER)));
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);
    boolean isAuthenticated = User.isAuthenticated();
    assertThat(isAuthenticated).isTrue();
  }

  @Test
  public void testAnonymousIsNotAuthenticated() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(Authorities.ANONYMOUS));
    securityContext.setAuthentication(
        new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities));
    SecurityContextHolder.setContext(securityContext);
    boolean isAuthenticated = User.isAuthenticated();
    assertThat(isAuthenticated).isFalse();
  }
}