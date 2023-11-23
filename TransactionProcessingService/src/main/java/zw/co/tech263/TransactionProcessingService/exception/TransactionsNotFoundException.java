package zw.co.tech263.TransactionProcessingService.exception;

public class TransactionsNotFoundException extends Exception {
    public TransactionsNotFoundException() {
        super();
    }

    public TransactionsNotFoundException(String message) {
        super(message);
    }

    public TransactionsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
