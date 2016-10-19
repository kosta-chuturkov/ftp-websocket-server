package ftp.core.constants;

public class APIAliases {

    public static final String ROOT_PREFIX = "/api";

    public static final String DELETE_FILE_ALIAS = "/files/delete/{deleteHash}";

    public static final String DOWNLOAD_FILE_ALIAS = "/files/";

    public static final String PROFILE_PIC_ALIAS = "/profilePic/";

    public static final String GET_FILES_SHARED_WITH_ME_ALIAS = "/files/shared/*";

    public static final String GET_PRIVATE_FILES_ALIAS = "/files/private/*";

    public static final String GET_UPLOADED_FILES_ALIAS = "/files/uploaded/*";

    public static final String QUERY_USERS_BY_NICK_NAME_ALIAS = ROOT_PREFIX + "/users*";

    public static final String UPDATE_USERS_FILE_IS_SHARED_TO_ALIAS = "/files/updateUsers/{deleteHash}";

    public static final String LOGIN_ALIAS = ROOT_PREFIX + "/login";

    public static final String LOGOUT_ALIAS = ROOT_PREFIX + "/logout";

    public static final String MAIN_PAGE_ALIAS = ROOT_PREFIX + "/main/";

    public static final String REGISTRATION_ALIAS = ROOT_PREFIX + "/register";

    public static final String UPLOAD_FILE_ALIAS = ROOT_PREFIX + "/upload/";

    public static final String UPLOAD_PAGE_ALIAS = ROOT_PREFIX + "/pageupload";

}
