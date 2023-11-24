package zw.co.tech263.CustomerSupportService.exception;

public class DuplicateAccountException extends Exception {
    public DuplicateAccountException() {
        super();
    }

    public DuplicateAccountException(String message) {
        super(message);
    }

    public DuplicateAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}