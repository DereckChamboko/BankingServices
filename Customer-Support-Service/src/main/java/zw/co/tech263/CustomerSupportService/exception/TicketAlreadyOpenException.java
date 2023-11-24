package zw.co.tech263.CustomerSupportService.exception;

public class TicketAlreadyOpenException extends Exception {
    public TicketAlreadyOpenException() {
        super();
    }

    public TicketAlreadyOpenException(String message) {
        super(message);
    }

    public TicketAlreadyOpenException(String message, Throwable cause) {
        super(message, cause);
    }
}