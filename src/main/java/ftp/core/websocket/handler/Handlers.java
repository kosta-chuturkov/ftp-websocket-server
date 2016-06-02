package ftp.core.websocket.handler;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */
public enum Handlers {

    DEFAULT_HANDLER("DEFAULT"),
    PRIVATE_FILE_HANDLER("getPrivateFiles"),
    SHARED_FILE_HANDLER("getSharedFiles"),
    SHARED_FILES_WITH_USERS_HANDLER("getSharedWithUsersFiles"),
    DELETED_FILE("deletedFile");

    private final String handlerName;

    Handlers(final String handlerName) {
        this.handlerName = handlerName;
    }

    public String getHandlerName() {
        return this.handlerName;
    }


}
