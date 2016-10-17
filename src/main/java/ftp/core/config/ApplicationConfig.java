package ftp.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan("ftp.core.*")
@ImportResource(value = "classpath:applicationContext.xml")
@EnableWebMvc
public class ApplicationConfig{

}
