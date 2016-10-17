package ftp.core.base;

import ftp.core.config.ApplicationConfig;
import ftp.core.model.entities.User;
import ftp.core.profiles.Profiles;
import ftp.core.service.face.tx.UserService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(value = { Profiles.TEST })
@ContextConfiguration(classes = ApplicationConfig.class)
@WebAppConfiguration
public abstract class AbstractTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

    protected User user;

    protected MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.user = this.userService.registerUser("admin@gmail.com","admin1234","admin","admin1234");
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }
}
