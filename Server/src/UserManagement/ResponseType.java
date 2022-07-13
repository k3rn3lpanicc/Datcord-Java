package UserManagement;

import java.io.Serializable;

public enum ResponseType implements Serializable {
    OK,
    USERNAME_INVALID,
    PASSWORD_INVALID,
    USER_NOT_FOUND,
    ACCESS_DENIED,
    INVALID_VERIFICATION,
    ERROR,
    TOKEN_CHANGED,
    TOKEN_INVALID,
    CALL_DOESNT_EXIST,
    SQLERROR,
    SERVER_NOT_FOUND,
    USER_ALREADY_EXIST,
    SERVER_ALREADY_EXISTS,
    CHAT_ALREADY_EXISTS,

}
