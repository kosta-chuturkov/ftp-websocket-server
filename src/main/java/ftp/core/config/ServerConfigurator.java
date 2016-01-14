package ftp.core.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;

import ftp.core.common.util.ServerConstants;

public class ServerConfigurator {

	public static Map<String, String> CONTENT_TYPES;

	private static File serverStorageFile;

	private ServerConfigurator() {
		CONTENT_TYPES = new HashMap<String, String>();
	}

	private final Logger logger = Logger.getLogger(ServerConfigurator.class);
	
	@PostConstruct
	private void init() {
		logger.error("********************************************ddddddddddddddddddddddddddddddddddd***************");
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
			InputStream is = this.getClass().getResourceAsStream(ServerConstants.CONTENT_TYPES_FILE);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] params = line.split(" ");
				CONTENT_TYPES.put(params[1], params[0]);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public static File getServerStorageFile() {
		return serverStorageFile;
	}

}
