package ftp.core.service.impl;

import ftp.core.config.FtpConfigurationProperties;
import ftp.core.profiles.Profiles;
import ftp.core.service.face.StorageService;
import ftp.core.util.ServerUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileDeleteStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

@Service
//@Profile(value = {Profiles.DEVELOPMENT, Profiles.PRODUCTION})
public class FileSystemStorageService implements StorageService {

  private final Path rootLocation;
  private final Path profilePictureLocation;

  private ApplicationContext context;

  @Autowired
  public FileSystemStorageService(FtpConfigurationProperties ftpConfigurationProperties,
      ApplicationContext context) {
    this.context = context;
    String rootStorageFolderName = ftpConfigurationProperties.getStorage()
        .getRootStorageFolderName();
    this.rootLocation = Paths.get(rootStorageFolderName);
    this.profilePictureLocation = Paths.get(rootStorageFolderName,
        ftpConfigurationProperties.getStorage().getProfilePicturesFolderName());
  }

  @PostConstruct
  public void initialize() {
    UUID.randomUUID();
    String multipartFileLocation = this.context.getEnvironment()
        .getProperty("spring.http.multipart.location");
    if (multipartFileLocation != null) {
      File tempFlder = new File(multipartFileLocation);
      if (!tempFlder.exists()) {
        tempFlder.mkdirs();
      }
    }
    init();
  }

  @Override
  public Long store(InputStream inputStream, String newFileName, String destinationFolder) {
    try {
      File dest = Paths.get(this.rootLocation.toFile().getAbsolutePath(), destinationFolder)
          .toFile();
      createDirIfNotExsists(dest);
      return copyFileToDestination(inputStream, dest.toPath().resolve(newFileName),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Failed to store file " + newFileName, e);
    }
  }

  @Override
  public Long storeProfilePicture(InputStream inputStream, String fileName) {
    try {
      StandardCopyOption replaceExisting = StandardCopyOption.REPLACE_EXISTING;
      return copyFileToDestination(inputStream, this.profilePictureLocation.resolve(fileName),
          replaceExisting);
    } catch (IOException e) {
      throw new RuntimeException("Failed to store file " + fileName, e);
    }
  }

  private Long copyFileToDestination(InputStream inputStream, Path destination,
      StandardCopyOption replaceExisting) throws IOException {
    try {
      return Files.copy(inputStream, destination, replaceExisting);
    } finally {
      inputStream.close();
    }
  }

  public Path load(String filename, String destinationFolder) {
    Path path = Paths.get(this.rootLocation.toFile().getAbsolutePath(), destinationFolder);
    return path.resolve(filename);
  }

  @Override
  public Resource loadAsResource(String filename, String destinationFolder)
      throws FileNotFoundException {
    Resource resource = loadAsResource(load(filename, destinationFolder));
    if (!ServerUtil.existsAndIsReadable(resource)) {
      throw new RuntimeException("Could not read file: " + resource.getFilename());
    }
    return resource;
  }

  @Override
  public void deleteResource(String filename, String destinationFolder) {
    try {
      File toDelete = load(filename, destinationFolder).toFile();
      if (toDelete.exists()) {
        FileDeleteStrategy.FORCE.deleteQuietly(toDelete);
      }
    } catch (Exception e) {
      throw new RuntimeException("Unable to delete resource[" + filename + "]");
    }
  }

  @Override
  public Resource loadAsResource(Path resourcePath) {
    try {
      Resource resource = new UrlResource(resourcePath.toUri());
      return resource;
    } catch (MalformedURLException e) {
      throw new RuntimeException("Could not read file: " + resourcePath.toFile().getName(), e);
    }
  }


  @Override
  public Resource loadProfilePicture(String userName) {
    return loadAsResource(this.profilePictureLocation.resolve(userName.concat(".jpg")));
  }

  private void init() {
    try {
      createDirIfNotExsists(this.rootLocation.toFile());
      createDirIfNotExsists(this.profilePictureLocation.toFile());
    } catch (Exception e) {
      throw new RuntimeException("Could not initialize storage", e);
    }
  }

  private void createDirIfNotExsists(File profilePictureLocation) {
    if (!profilePictureLocation.exists()) {
      profilePictureLocation.mkdir();
    }
  }
}
