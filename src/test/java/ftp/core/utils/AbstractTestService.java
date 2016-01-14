package ftp.core.utils;

import javax.annotation.Resource;

import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import ftp.core.common.model.User;
import ftp.core.service.face.tx.UserService;

@Service
public class AbstractTestService {
	@Resource
	private UserService userService;
	
	//@Resource(name = "sessionFactory")
	//private LocalSessionFactoryBean annotationSessionFactoryBean;

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	private static User owner;
	private static boolean firstRun = true;

	public void initDB() {
		setScope();
		clearDB();

		if (firstRun) {
			firstRun = false;
		} else {
		}
		setUser();
	}

	public void setScope() {
		ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
		Scope requestScope = new SimpleThreadScope();
		beanFactory.registerScope("request", requestScope);
		Scope sessionScope = new SimpleThreadScope();
		beanFactory.registerScope("session", sessionScope);
	}

	@After
	public void clearUser() {
		User.setCurrent(null);
	}

	public void clearDB() {
		//annotationSessionFactoryBean.dropDatabaseSchema();
		//annotationSessionFactoryBean.createDatabaseSchema();
	}

	public void setUser() {
		owner = (User) userService.findOne(
				userService.registerUser("system@abv.bg", "passhash", "system", "passhash", new ModelAndView()));
		User.setCurrent(owner);
	}
}
