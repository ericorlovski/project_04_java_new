package kz.bcc.dbpjunioraccountmanageservice.exception;

public class AuthErrorException extends RuntimeException {

    public AuthErrorException() {
    }

    public AuthErrorException(String message) {
        super(message);
    }

    public AuthErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthErrorException(Throwable cause) {
        super(cause);
    }

    public AuthErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
