package ftp.core.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * Created by Kosta_Chuturkov on 2/3/2016.
 */
@SpringBootApplication(scanBasePackages = "ftp.core")
public class BootLoader extends SpringBootServletInitializer {

    /**
     * Starts the spring boot application
     *
     * @param args
     */
    public static void main(final String[] args) {
        SpringApplication.run(BootLoader.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(BootLoader.class);
    }

}
