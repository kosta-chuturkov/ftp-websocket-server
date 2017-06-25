package ftp.core;

import ftp.core.config.FtpConfigurationProperties;
import ftp.core.profiles.Profiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties({FtpConfigurationProperties.class})
public final class BootLoader {

    private static final Logger log = LoggerFactory.getLogger(BootLoader.class);

    private static final String SPRING_PROFILE_DEFAULT = "spring.profiles.default";


    /**
     * Starts the spring boot application
     *
     * @param args
     */
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(BootLoader.class);
        setDefaultProfile(app, Profiles.DEVELOPMENT);
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

    public static void setDefaultProfile(SpringApplication app, String profile) {
        Map<String, Object> defProperties =  new HashMap<>();
        /*
        * The default profile to use when no other profiles are defined
        * This cannot be set in the <code>application.yml</code> file.
        * See https://github.com/spring-projects/spring-boot/issues/1219
        */
        defProperties.put(SPRING_PROFILE_DEFAULT, profile);
        app.setDefaultProperties(defProperties);
    }


}
