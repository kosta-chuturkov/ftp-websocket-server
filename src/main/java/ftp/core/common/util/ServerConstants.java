package ftp.core.common.util;

public interface ServerConstants {


    String REGISTRATION_PAGE = "RegistrationPage";

    String NEW_CLIENT_LOGIN_PAGE = "NewClientPage";

    String RESOURCE_NOT_FOUND_PAGE = "ResourceNotFoundPage";

    String UPLOAD_PAGE = "UploadPage";

    String MAIN_PAGE = "MainPage";

    String MAIN_ALIAS = "/main/";

    String LOGIN_ALIAS = "/login";

    String FILES_ALIAS = "/files/";

    String PROFILE_PIC_ALIAS = "/profilePic/";

    String PROFILE_PIC_FOLDER = "profilePictures";

    String DELETE_ALIAS = "delete/";

    String UPDATE_ALIAS = "updateUsers/";

    String EMAIL_PARAMETER = "email";

    String JSESSIONID_PARAMETER = "JSESSIONID";

    String SESSION_ID_PARAMETER = "sessionId";

    String STORAGE_PARAMETER = "storage";

    String MAX_STORAGE_PARAMETER = "maxStorage";

    String PASSWORD = "PREFID";

    String SERVER_STORAGE_FOLDER_NAME = "D:/ServerFileStorage";

    int DEFAULT_BUFFER_SIZE = 1024;

    int MAXIMUM_PASSWORD_lENGTH = 64;

    String CONTENT_TYPES_FILE = "/resources/content_types.txt";

    String USER_REGEX = "[A-Za-z][A-Za-z0-9._]*";

    long UPLOAD_LIMIT = 20 * (1024l * 1024l * 1024l);

    String HOST = "host";

    String PORT = "port";

    String PROFILE_PICTURE_PARAM = "profilePictureAddress";

    final static String CURRENT_USER = "currentUser";

    int MINIMUM_PASSWORD_LENGTH = 6;

    long USER_MAX_UPLOAD_IN_BYTES = UPLOAD_LIMIT;

    String DELETE_SALT = "1RWqnVnlyrDTSiLqKtXuyGzRu8Eqi2vmJIfEKteBM8gE99F4WnJ79N1reqp1";

    String DOWNLOAD_SALT = "P5esIq0RXXvqZigZfdUSUzatUcVotQTQCx8HKLT83tUVVScq2oymCiQZ2N26kBgpfC5HANyZheJaiaQ4";

}
