package ftp.core.config;

import static org.mockito.Mockito.when;

import ftp.core.constants.ServerConstants;
import ftp.core.profiles.Profiles;
import ftp.core.service.face.StorageService;
import java.io.IOException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;

/**
 * Created by Kosta_Chuturkov on 11/2/2016.
 */
@Configuration
@Profile(value = {Profiles.TEST})
public class TestConfiguration {


  private ResourceLoader resourceLoader;

  @Autowired
  public TestConfiguration(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @Bean
  public StorageService storageService() {
    org.springframework.core.io.Resource defaultPic = this.resourceLoader
        .getResource(ServerConstants.DEFAULT_PROFILE_PICTURE);
    StorageService storageService = Mockito.mock(StorageService.class);
    try {
      when(storageService.loadProfilePicture(ArgumentMatchers.anyString()))
          .thenReturn(new FileSystemResource(defaultPic.getFile()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return storageService;
  }
}
