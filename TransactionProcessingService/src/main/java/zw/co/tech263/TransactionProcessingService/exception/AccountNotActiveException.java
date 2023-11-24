package zw.co.tech263.TransactionProcessingService.exception;

public class AccountNotActiveException extends Exception {
    public AccountNotActiveException() {
        super();
    }

    public AccountNotActiveException(String message) {
        super(message);
    }

    public AccountNotActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
