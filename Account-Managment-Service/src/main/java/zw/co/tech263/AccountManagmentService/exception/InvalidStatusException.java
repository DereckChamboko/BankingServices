package zw.co.tech263.AccountManagmentService.exception;

public class InvalidStatusException extends Exception {
    public InvalidStatusException() {
        super();
    }

    public InvalidStatusException(String message) {
        super(message);
    }

    public InvalidStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}

