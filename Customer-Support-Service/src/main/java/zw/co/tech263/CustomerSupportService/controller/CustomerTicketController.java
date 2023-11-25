package zw.co.tech263.CustomerSupportService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.tech263.CustomerSupportService.dto.request.ActivityDTO;
import zw.co.tech263.CustomerSupportService.dto.request.CommentDTO;
import zw.co.tech263.CustomerSupportService.dto.request.CustomerAccountDTO;
import zw.co.tech263.CustomerSupportService.dto.respose.ErrorResponse;
import zw.co.tech263.CustomerSupportService.dto.request.OpenTicketDTO;
import zw.co.tech263.CustomerSupportService.exception.*;
import zw.co.tech263.CustomerSupportService.model.*;
import zw.co.tech263.CustomerSupportService.service.CustomerAccountService;
import zw.co.tech263.CustomerSupportService.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CustomerTicketController {

    private final CustomerAccountService customerAccountService;
    private final TicketService ticketService;

    @Autowired
    public CustomerTicketController(CustomerAccountService customerAccountService, TicketService ticketService) {
        this.customerAccountService = customerAccountService;
        this.ticketService = ticketService;
    }

    // Create a customer account
    @PostMapping("/customer-accounts")
    public ResponseEntity<CustomerAccount> createCustomerAccount(@RequestBody CustomerAccountDTO customerAccount) throws DuplicateAccountException {
        CustomerAccount createdAccount = customerAccountService.createCustomerAccount(customerAccount);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    // Add a new ticket
    @PostMapping("/tickets")
    public ResponseEntity<Ticket> createTicket(@RequestBody OpenTicketDTO openTicketDTO) throws TicketCategoryNotFoundException, AccountNotFoundException {
        Ticket createdTicket = ticketService.createTicket(openTicketDTO);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    // Get all tickets
    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }
    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<Ticket> getTicketsAccountNumber(@PathVariable("ticketId") String ticketId) throws TicketNotFoundException {
        Ticket tickets = ticketService.getTicketById(ticketId);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    // Resolve a ticket
    @PutMapping("/tickets/{ticketId}/resolve")
    public ResponseEntity<Ticket> resolveTicket(@PathVariable("ticketId") String ticketId,@RequestParam("resolve") String resolve,@RequestParam("user") String user) throws TicketAlreadyResolvedException, TicketNotFoundException {
        Ticket resolvedTicket = ticketService.resolveTicket(ticketId,resolve,user);
        return new ResponseEntity<>(resolvedTicket, HttpStatus.OK);
    }

    // Reopen a closed ticket
    @PutMapping("/tickets/{ticketId}/reopen")
    public ResponseEntity<Ticket> reopenTicket(@PathVariable("ticketId") String ticketId,@RequestParam("reason") String reason,@RequestParam("user") String user) throws TicketAlreadyOpenException, TicketNotFoundException {
        Ticket reopenedTicket = ticketService.reopenTicket(ticketId,reason,user);
        return new ResponseEntity<>(reopenedTicket, HttpStatus.OK);
    }

    // Add a comment to a ticket
    @PostMapping("/tickets/{ticketId}/comments")
    public ResponseEntity<Ticket> addCommentToTicket(@PathVariable("ticketId") String ticketId, @RequestBody CommentDTO comment) throws TicketNotFoundException {
        Ticket ticketWithComment = ticketService.addCommentToTicket(ticketId, comment);
        return new ResponseEntity<>(ticketWithComment, HttpStatus.OK);
    }

    // Add an activity to a ticket
    @PostMapping("/tickets/{ticketId}/activities")
    public ResponseEntity<Ticket> addActivityToTicket(@PathVariable("ticketId") String ticketId, @RequestBody ActivityDTO activityDto) throws TicketNotFoundException {

        Ticket ticketWithActivity = ticketService.addActivityToTicket(ticketId, activityDto);
        return new ResponseEntity<>(ticketWithActivity, HttpStatus.OK);
    }



    @ExceptionHandler({DuplicateAccountException.class,TicketAlreadyOpenException.class,TicketAlreadyResolvedException.class, TicketCategoryNotFoundException.class, TicketNotFoundException.class, AccountNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleAccountExceptions(Exception ex) {
        ErrorResponse errorResponse= ErrorResponse.builder()
                .errorDescription(ex.getMessage())
                .errorCode("400")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
