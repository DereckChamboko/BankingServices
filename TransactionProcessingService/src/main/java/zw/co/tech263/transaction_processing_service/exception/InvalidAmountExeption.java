package zw.co.tech263.transaction_processing_service.exception;

public class InvalidAmountExeption extends Exception {
    public InvalidAmountExeption() {
        super();
    }

    public InvalidAmountExeption(String message) {
        super(message);
    }

    public InvalidAmountExeption(String message, Throwable cause) {
        super(message, cause);
    }
}
