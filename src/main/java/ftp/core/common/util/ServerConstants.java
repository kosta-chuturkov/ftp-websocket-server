package ftp.core.common.util;

public interface ServerConstants {


    public static final String REGISTRATION_PAGE = "RegistrationPage";

    public static final String NEW_CLIENT_LOGIN_PAGE = "NewClientPage";

    public static final String RESOURCE_NOT_FOUND_PAGE = "ResourceNotFoundPage";

    public static final String UPLOAD_PAGE = "UploadPage";

    public static final String MAIN_PAGE = "MainPage";

    public static final String MAIN_ALIAS = "/main/";

    public static final String LOGIN_ALIAS = "/login";

    public static final String FILES_ALIAS = "/files/";

    public static final String DELETE_ALIAS = "delete/";

    public static final String UPDATE_ALIAS = "updateUsers/";

    public static final String RESOURCE_FOLDER_NAME = "resources";

    public static final String QUERY_PARAMETER = "q";

    public static final String DATA_PARAMETER = "data";

    public static final String EMAIL_PARAMETER = "email";

    public static final String NICK_NAME_PARAMETER = "nickName";

    public static final String FILE_NAME_PARAMETER = "fileName";

    public static final String MODIFIER_PARAMETER = "modifier";

    public static final String ERROR_MESSAGE_PARAMETER = "errorMsg";

    public static final String JSESSIONID_PARAMETER = "JSESSIONID";

    public static final String SESSION_ID_PARAMETER = "sessionId";

    public static final String CONTROLLER_PARAMETER = "controller";

    public static final String STORAGE_PARAMETER = "storage";

    public static final String MAX_STORAGE_PARAMETER = "maxStorage";

    public static final String MAXIMUM_STORAGE = "maxFileSize";

    public static final String PASSWORD = "PREFID";

    final static String CURRENT_USER = "currentUser";

    public static final String PASSWORD_REPEATED_PARAMETER = "pswdRepeated";

    public static final String SERVER_STORAGE_FOLDER_NAME = "D:/ServerFileStorage";

    public static final String GO_TO_PARENT_FOLDER_COMMAND = "/..";

    public static final int DEFAULT_BUFFER_SIZE = 1024;

    public static final int MAXIMUM_USER_NAME_lENGTH = 30;

    public static final int MAXIMUM_PASSWORD_lENGTH = 64;

    public static final String CONTENT_TYPES_FILE = "/resources/content_types.txt";

    public static final int NAME = 1;

    public static final int TIMESTAMP = 0;

    public static final int MAX_FILE_NAME_LENGTH = 222;

    public static final long MAX_FILE_SIZE = (1024l * 1024l * 1024l);

    public static final String USER_REGEX = "[A-Za-z][A-Za-z0-9._]*";

    public static final long TWENTY_MEGABYTES = 20971520l;

    public static final long FIVE_HUNDRED_MEGABYTES = 524288000l;

    public static final long UPLOAD_LIMIT = (1024l * 1024l * 1024l * 1024l);

    public static final String HOST = "host";

    public static final String PORT = "port";

    public static final int ALL_ENTRIES = 0;

    public static final int MINIMUM_PASSWORD_LENGTH = 6;

    public static final int MINIMUM_EMAIL_LENGTH = 3;

    public static final long USER_MAX_UPLOAD_IN_BYTES = (1024l * 1024l * 1024l * 1024l);

    public static final String DELETE_SALT = "1RWqnVnlyrDTSiLqKtXuyGzRu8Eqi2vmJIfEKteBM8gE99F4WnJ79N1reqp1";

    public static final String DOWNLOAD_SALT = "P5esIq0RXXvqZigZfdUSUzatUcVotQTQCx8HKLT83tUVVScq2oymCiQZ2N26kBgpfC5HANyZheJaiaQ4";

}
