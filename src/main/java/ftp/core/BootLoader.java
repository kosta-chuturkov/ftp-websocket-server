package ftp.core;

import ftp.core.config.FtpConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@ComponentScan
@EnableAutoConfiguration
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
        Environment env = app.run(args).getEnvironment();
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\thttp://127.0.0.1:{}\n\t" +
                        "External: \thttp://{}:{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));

    }


}
