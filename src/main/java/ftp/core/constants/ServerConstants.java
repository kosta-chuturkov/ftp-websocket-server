package ftp.core.constants;

public class ServerConstants {


    public static final String CURRENT_USER = "currentUser";
    public static final String REGISTRATION_PAGE = "RegistrationPage";
    public static final String NEW_CLIENT_LOGIN_PAGE = "NewClientPage";
    public static final String RESOURCE_NOT_FOUND_PAGE = "ResourceNotFoundPage";
    public static final String UPLOAD_PAGE = "UploadPage";
    public static final String MAIN_PAGE = "MainPage";
    public static final String PROFILE_PIC_FOLDER = "profilePictures";
    public static final String DELETE_ALIAS = "delete/";
    public static final String EMAIL_PARAMETER = "email";
    public static final String JSESSIONID_PARAMETER = "JSESSIONID";
    public static final String SESSION_ID_PARAMETER = "sid";
    public static final String STORAGE_PARAMETER = "storage";
    public static final String MAX_STORAGE_PARAMETER = "maxStorage";
    public static final String PASSWORD = "PREFID";
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    public static final int MAXIMUM_PASSWORD_lENGTH = 64;
    public static final String CONTENT_TYPES_FILE = "classpath:/static/content_types.txt";
    public static final String DEFAULT_PROFILE_PICTURE = "classpath:/static/unknown_user.png";
    public static final String USER_REGEX = "[A-Za-z][A-Za-z0-9._]*";
    public static final long UPLOAD_LIMIT = 20 * (1024l * 1024l * 1024l);
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String PROFILE_PICTURE_PARAM = "profilePictureAddress";
    public static final int MINIMUM_PASSWORD_LENGTH = 6;

    public static final long USER_MAX_UPLOAD_IN_BYTES = UPLOAD_LIMIT;

    public static final String DELETE_SALT = "1RWqnVnlyrDTSiLqKtXuyGzRu8Eqi2vmJIfEKteBM8gE99F4WnJ79N1reqp1";

    public static final String REMEMBER_ME_SECURITY_KEY = "5c37379956bd1242f5636c8cb322c2966ad82277";

    public static final String DOWNLOAD_SALT = "P5esIq0RXXvqZigZfdUSUzatUcVotQTQCx8HKLT83tUVVScq2oymCiQZ2N26kBgpfC5HANyZheJaiaQ4";

}
