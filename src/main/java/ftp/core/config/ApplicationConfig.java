package ftp.core.config;

import com.google.common.collect.Maps;
import ftp.core.constants.ServerConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
import java.net.InetAddress;
import java.net.UnknownHostException;
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
        setServerAddress(this.applicationContext.getEnvironment());
    }

    private String serverAddress;

    public String setServerAddress(Environment env) {
        String hostAddress = extractHostAddress();
        String serverPort = env.getProperty("server.port", "80");
        return this.serverAddress = new StringBuilder(determineHttpOrHttps(env)).append(hostAddress).append(":").append(serverPort).toString();
    }

    private String extractHostAddress() {
        String hostAddress;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return hostAddress;
    }

    private String determineHttpOrHttps(Environment env) {
        String secure = env.getProperty("server.ssl.enabled", "false");
        return secure.equals("true") ? "https://" : "http://";
    }

    public String getServerAddress() {
        return this.serverAddress;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/resources/**")
                .addResourceLocations("classpath:/static/");
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
            final InputStream is = this.applicationContext.getResource(ServerConstants.CONTENT_TYPES_FILE).getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] params = line.split(" ");
                this.contentTypes.put(params[1], params[0]);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Map<String, String> getContentTypes() {
        return this.contentTypes;
    }


    private String getHostAddress() {
        String hostAddress;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return hostAddress;
    }


}
