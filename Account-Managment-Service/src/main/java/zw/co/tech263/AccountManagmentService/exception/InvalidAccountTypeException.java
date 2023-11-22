package zw.co.tech263.AccountManagmentService.exception;

public class InvalidAccountTypeException extends Exception {
    public InvalidAccountTypeException() {
        super();
    }

    public InvalidAccountTypeException(String message) {
        super(message);
    }

    public InvalidAccountTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}

