package ftp.core;

import ftp.core.model.entities.User;
import ftp.core.profiles.Profiles;
import ftp.core.service.face.tx.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.sql.Statement;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(value = {Profiles.TEST})
@SpringBootTest(classes = BootLoader.class)
public abstract class AbstractTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

    @Autowired
    private DataSource dataSource;

    protected User user;

    protected MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.user = this.userService.registerUser("admin@gmail.com", "admin1234", "admin", "admin1234");
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @After
    public void cleanup() throws Exception {
        Assert.isTrue(
                this.dataSource.getConnection().getMetaData().getDriverName().equals("HSQL Database Engine Driver"),
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
