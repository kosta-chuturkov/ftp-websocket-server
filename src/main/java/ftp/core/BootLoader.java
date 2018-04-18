package ftp.core;

import ftp.core.config.DefaultProfileUtil;
import ftp.core.config.FtpConfigurationProperties;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication(exclude = {ValidationAutoConfiguration.class,
    PersistenceExceptionTranslationAutoConfiguration.class, SpringApplicationAdminJmxAutoConfiguration.class })
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
        log.info("VM args:");
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> listOfArguments = runtimeMxBean.getInputArguments();
        listOfArguments.forEach(arg -> log.info("ARG: {}", arg));
    }

}
