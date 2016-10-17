package ftp.core.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by Kosta_Chuturkov on 2/3/2016.
 */
@SpringBootApplication(scanBasePackages = "ftp.core")
public class BootLoader {

    /**
     * Starts the spring boot application
     *
     * @param args
     */
    public static void main(final String[] args) {
        SpringApplication.run(BootLoader.class, args);
    }


}
