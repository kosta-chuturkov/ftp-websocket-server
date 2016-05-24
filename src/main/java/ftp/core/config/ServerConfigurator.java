package ftp.core.config;

import ftp.core.common.util.ServerConstants;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ServerConfigurator {

    public static Map<String, String> CONTENT_TYPES;

    private static File serverStorageFile;

    private ServerConfigurator() {
        CONTENT_TYPES = new HashMap<String, String>();
    }

    private final Logger logger = Logger.getLogger(ServerConfigurator.class);

    @PostConstruct
    private void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
        createServerStorage();
        fillContentTypeTable();
    }

    private void createServerStorage() {
        serverStorageFile = new File(ServerConstants.SERVER_STORAGE_FOLDER_NAME);
        if (!serverStorageFile.exists()) {
            serverStorageFile.mkdir();
        }
    }

    private void fillContentTypeTable() {
        try {
            final InputStream is = this.getClass().getResourceAsStream(ServerConstants.CONTENT_TYPES_FILE);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                final String[] params = line.split(" ");
                CONTENT_TYPES.put(params[1], params[0]);
            }
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static File getServerStorageFile() {
        return serverStorageFile;
    }

}
