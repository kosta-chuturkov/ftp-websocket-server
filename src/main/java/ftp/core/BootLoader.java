package ftp.core;

import ftp.core.config.DefaultProfileUtil;
import ftp.core.config.FtpConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootApplication(exclude = {ValidationAutoConfiguration.class,
    PersistenceExceptionTranslationAutoConfiguration.class })
@EnableConfigurationProperties({FtpConfigurationProperties.class})

public class BootLoader {

    private static final Logger log = LoggerFactory.getLogger(BootLoader.class);

    /**
     * Starts the spring boot application
     *
     * @param args
     */
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(BootLoader.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\thttp://127.0.0.1:{}\n\t" +
                        "External: \thttp://{}:{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                hostAddress,
                env.getProperty("server.port"));

    }

}
