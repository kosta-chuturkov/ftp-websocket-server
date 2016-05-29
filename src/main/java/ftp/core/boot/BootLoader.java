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

    public static void main(final String[] args) {

        final ConfigurableApplicationContext run = SpringApplication.run(BootLoader.class, args);
        System.out.println(run);

    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        final String str = "<div class=\"s2-example\">\n" +
                "         <p>\n" +
                "            <select class=\"js-example-data-array\" tabindex=\"-1\" aria-hidden=\"true\">\n" +
                "               <option value=\"0\">2e3</option>\n" +
                "               <option value=\"1\">bug</option>\n" +
                "               <option value=\"2\">duplicate</option>\n" +
                "               <option value=\"3\">invalid</option>\n" +
                "               <option value=\"4\">wontfix</option>\n" +
                "            </select>\n" +
                "            <span class=\"select2 select2-container select2-container--default\" dir=\"ltr\" style=\"width: 100%;\">\n" +
                "\t\t\t<span class=\"selection\">\n" +
                "\t\t\t</span>\n" +
                "\t\t\t</span>\n" +
                "\t\t\t<span class=\"dropdown-wrapper\" aria-hidden=\"true\"></span></span>\n" +
                "         </p>\n" +
                "      </div>";
        return application.sources(BootLoader.class);
    }

}
