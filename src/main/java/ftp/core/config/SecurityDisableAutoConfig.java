package ftp.core.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "security.enabled", havingValue = "false")
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SecurityDisableAutoConfig {
}
