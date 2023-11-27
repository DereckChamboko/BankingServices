package zw.co.tech263.CustomerSupportService.exception;

public class TicketCategoryNotFoundException extends Exception {
    public TicketCategoryNotFoundException() {
        super();
    }

    public TicketCategoryNotFoundException(String message) {
        super(message);
    }

    public TicketCategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}