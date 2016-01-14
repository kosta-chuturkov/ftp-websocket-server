package ftp.core.service;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ftp.core.common.model.User;
import ftp.core.service.face.tx.UserService;
import ftp.core.utils.AbstractTestService;

@ContextConfiguration(locations = { "classpath:persistence/hibernate-ApplicationContext.xml",
		"classpath:persistence/dao-ApplicationContext.xml", "classpath:service/service-ApplicationContext.xml",
		 "classpath:service/service-test-ApplicationContext.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ "test" })
public class AbstractTest {

	@Resource
	protected UserService userService;

	@Resource
	private AbstractTestService abstractTestService;

	@Before
	public void initDB() {
		abstractTestService.initDB();
	}

	@After
	public void clearUser() {
		User.setCurrent(null);
	}

}
