package ftp.core.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by Kosta_Chuturkov on 2/3/2016.
 */
@SpringBootApplication(scanBasePackages = "ftp.core", exclude = {
		org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class

})
@ImportResource(value = "classpath:applicationContext.xml")
@EnableWebMvc
public class BootLoader extends SpringBootServletInitializer {

	public static void main(String[] args) {

		final ConfigurableApplicationContext run = SpringApplication.run(BootLoader.class, args);
		System.out.println(run);

	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

		return application.sources(BootLoader.class);
	}

}
