package ftp.core.config;

import ftp.core.model.entities.User;
import ftp.core.repository.UserRepository;
import ftp.core.service.face.tx.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<User> auditorAware() {
        return new CustomAuditorAware();
    }

    public class CustomAuditorAware implements AuditorAware<User> {
        @Override
        public Optional<User> getCurrentAuditor() {
            return Optional.of(User.getCurrent());
        }
    }
}
