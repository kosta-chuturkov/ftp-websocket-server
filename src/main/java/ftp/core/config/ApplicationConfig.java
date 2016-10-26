package ftp.core.config;

import com.google.common.collect.Maps;
import ftp.core.constants.ServerConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TimeZone;

@Configuration
@EnableJpaRepositories("ftp.core.repository")
@EnableTransactionManagement
public class ApplicationConfig extends WebMvcConfigurerAdapter {

    private Map<String, String> contentTypes = Maps.newHashMap();

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    private void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
        fillContentTypeTable();
    }


    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/resources/**")
                .addResourceLocations("classpath:/resources/");
    }

    @Bean
    public ViewResolver configureViewResolver() {
        InternalResourceViewResolver viewResolve = new InternalResourceViewResolver();
        viewResolve.setPrefix("/jsp/");
        viewResolve.setSuffix(".jsp");

        return viewResolve;
    }

    private void fillContentTypeTable() {
        try {
            final InputStream is = this.applicationContext.getResource(ServerConstants.CONTENT_TYPES_FILE).getInputStream(); //this.getClass().getResourceAsStream(ServerConstants.CONTENT_TYPES_FILE);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] params = line.split(" ");
                this.contentTypes.put(params[1], params[0]);
            }
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
    }


    public Map<String, String> getContentTypes() {
        return this.contentTypes;
    }


}
