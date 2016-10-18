package ftp.core.service.face;

import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;

public interface StorageService {

    void store(InputStream inputStream, String newFileName, String destinationFolder);

    void storeProfilePicture(InputStream inputStream, String fileName);

    /**
     * Loads a resource from the file system
     *
     * @param filename          he name of the file to load.
     * @param destinationFolder folder containing the file
     * @return loader resource
     * @throws FileNotFoundException if the file does not exist or is not readable
     */
    Resource loadAsResource(String filename, String destinationFolder) throws FileNotFoundException;

    void deleteResource(String filename, String destinationFolder);

    /**
     * Loads a resource form a given path. Allows checking if the resource exists
     *
     * @param resourcePath Path to load the resource from
     * @return loaded resource
     */
    Resource loadAsResource(Path resourcePath);

    Resource loadProfilePicture(String userName);
}
