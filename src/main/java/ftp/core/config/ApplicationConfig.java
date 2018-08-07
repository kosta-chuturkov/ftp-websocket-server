package ftp.core.config;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableJpaRepositories("ftp.core.repository")
@EnableTransactionManagement
@EnableScheduling
public class ApplicationConfig extends WebMvcConfigurerAdapter {

  private Map<String, String> contentTypes = Maps.newHashMap();

  @Autowired
  private ApplicationContext applicationContext;

  @PostConstruct
  private void init() {
    TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
    setServerAddress(this.applicationContext.getEnvironment());
  }

  private String serverAddress;

  public String setServerAddress(Environment env) {
    String hostAddress = extractHostAddress();
    String serverPort = env.getProperty("server.port", "80");
    return this.serverAddress = determineHttpOrHttps(env)
        + hostAddress
        + ":"
        + serverPort;
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

  @Bean
  public JpaTransactionManager transactionManager(final EntityManagerFactory emf) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(emf);
    return transactionManager;
  }

  @Bean
  public Gson gson() {
    return new GsonBuilder().serializeNulls().create();
  }
}
