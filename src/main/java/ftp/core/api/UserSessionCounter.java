package ftp.core.api;

/**
 * Counts the active user sessions and
 * if there are no more active user sessions disables
 * user websocket notifications
 */
public interface UserSessionCounter {

  void addUserSession(String userName);

  void removeUserSession(String userName);
}
