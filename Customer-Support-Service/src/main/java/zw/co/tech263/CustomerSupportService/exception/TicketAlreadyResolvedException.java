package zw.co.tech263.CustomerSupportService.exception;

public class TicketAlreadyResolvedException extends Exception {
    public TicketAlreadyResolvedException() {
        super();
    }

    public TicketAlreadyResolvedException(String message) {
        super(message);
    }

    public TicketAlreadyResolvedException(String message, Throwable cause) {
        super(message, cause);
    }
}