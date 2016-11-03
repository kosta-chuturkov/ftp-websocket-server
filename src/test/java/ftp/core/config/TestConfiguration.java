package ftp.core.config;

import ftp.core.profiles.Profiles;
import ftp.core.service.face.StorageService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by Kosta_Chuturkov on 11/2/2016.
 */
@Configuration
@Profile(value = {Profiles.TEST})
public class TestConfiguration {

    @Bean
    public StorageService storageService(){
        return Mockito.mock(StorageService.class);
    }
}
