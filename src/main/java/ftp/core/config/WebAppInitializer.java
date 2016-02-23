package ftp.core.config;

import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Created by Kosta_Chuturkov on 2/4/2016.
 */
@Configuration
public class WebAppInitializer implements WebApplicationInitializer, ApplicationContextAware {

	@Resource
	private ApplicationContext appContext;



	@Override
	public void onStartup(ServletContext container) {

		ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher",
				new DispatcherServlet((WebApplicationContext) appContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/*");
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {

		this.appContext = applicationContext;
	}
}
