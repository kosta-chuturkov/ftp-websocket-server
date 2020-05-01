package ftp.core.base;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.common.collect.Sets;
import ftp.core.BootLoader;
import ftp.core.config.FtpConfigurationProperties;
import ftp.core.model.entities.Authority;
import ftp.core.model.entities.User;
import ftp.core.profiles.Profiles;
import ftp.core.security.Authorities;
import ftp.core.service.face.tx.UserService;
import java.sql.Statement;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@ActiveProfiles(value = {Profiles.TEST})
@SpringBootTest(classes = BootLoader.class)
public abstract class AbstractTest {

  @Autowired
  private UserService userService;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private FtpConfigurationProperties ftpConfigurationProperties;

  public void setUp() {
    makeRequestsAs();
  }

  /**
   * Setup default user that will be calling the controllers/services. - Either Admin by calling
   * {@link AbstractTest#makeRequestsAdminUser} or as anonymous user by calling {@link
   * AbstractTest#makeRequestsAsAnonymousUser} on the {@link AbstractTest}
   */
  protected abstract void makeRequestsAs();

  protected void makeRequestsAdminUser() {
    User user = this.userService.registerUser("admin@gmail.com", "admin", "admin1234", "admin1234");
    assertThat(user, is(notNullValue()));
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
  }

  protected void makeRequestsAsAnonymousUser() {
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(new UsernamePasswordAuthenticationToken("anonymous", "anonymous",
        Sets.newHashSet(new Authority(Authorities.ANONYMOUS))));
  }

  protected void makeRequestAs(User user) {
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
  }

  public void cleanup() throws Exception {
    Assert.isTrue(
        this.dataSource.getConnection().getMetaData().getDriverName()
            .equals("HSQL Database Engine Driver"),
        "This @After method wipes the entire database! Do not use this on anything other than an in-memory database!");

    Statement databaseTruncationStatement = null;
    try {
      databaseTruncationStatement = this.dataSource.getConnection().createStatement();
      databaseTruncationStatement.executeUpdate("TRUNCATE SCHEMA public AND COMMIT");
    } finally {
      databaseTruncationStatement.close();
    }

  }
}
