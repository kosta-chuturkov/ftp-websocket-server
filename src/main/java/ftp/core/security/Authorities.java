package ftp.core.security;

/**
 * Constants for Spring Security authorities.
 */
public final class Authorities {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private Authorities() {
    }
}
