package ftp.core.service.impl;

import ftp.core.config.FtpConfigurationProperties;
import ftp.core.service.face.StorageService;
import org.apache.commons.io.FileDeleteStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final Path profilePictureLocation;


    @Autowired
    public FileSystemStorageService(FtpConfigurationProperties ftpConfigurationProperties) {
        String rootStorageFolderName = ftpConfigurationProperties.getStorage().getRootStorageFolderName();
        this.rootLocation = Paths.get(rootStorageFolderName);
        this.profilePictureLocation = Paths.get(rootStorageFolderName, ftpConfigurationProperties.getStorage().getProfilePicturesFolderName());
        init();
    }

    @Override
    public void store(InputStream inputStream, String newFileName, String destinationFolder) {
        try {
            File dest = Paths.get(this.rootLocation.toFile().getName(), destinationFolder).toFile();
            createDirIfNotExsists(dest);
            copyFileToDestination(inputStream, dest.toPath().resolve(newFileName), StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + newFileName, e);
        }
    }

    @Override
    public void storeProfilePicture(InputStream inputStream, String fileName) {
        try {
            StandardCopyOption replaceExisting = StandardCopyOption.REPLACE_EXISTING;
            copyFileToDestination(inputStream, this.profilePictureLocation.resolve(fileName), replaceExisting);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + fileName, e);
        }
    }

    private void copyFileToDestination(InputStream inputStream, Path destination, StandardCopyOption replaceExisting) throws IOException {
        Files.copy(inputStream, destination, replaceExisting);
        inputStream.close();
    }

    public Path load(String filename, String destinationFolder) {
        Path path = Paths.get(this.rootLocation.toFile().getName(), destinationFolder);
        return path.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename, String destinationFolder) throws FileNotFoundException {
        Resource resource = loadAsResource(load(filename, destinationFolder));
        if (!existsAndIsReadable(resource)) {
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

    private boolean existsAndIsReadable(Resource resource) {
        return resource.exists() || resource.isReadable();
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
