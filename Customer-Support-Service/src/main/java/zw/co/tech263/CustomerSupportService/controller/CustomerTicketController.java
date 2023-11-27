package zw.co.tech263.CustomerSupportService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.tech263.CustomerSupportService.dto.request.ActivityDTO;
import zw.co.tech263.CustomerSupportService.dto.request.CommentDTO;
import zw.co.tech263.CustomerSupportService.dto.respose.ErrorResponse;
import zw.co.tech263.CustomerSupportService.dto.request.OpenTicketDTO;
import zw.co.tech263.CustomerSupportService.exception.*;
import zw.co.tech263.CustomerSupportService.model.Ticket;
import zw.co.tech263.CustomerSupportService.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@Tag(name = "Customer Ticket Controller", description = "Endpoints for managing customer accounts and tickets")
public class CustomerTicketController {


    private final TicketService ticketService;

    @Autowired
    public CustomerTicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }



    @Operation(summary = "Add a new ticket")
    @PostMapping()
    public ResponseEntity<Ticket> createTicket(
            @RequestBody OpenTicketDTO openTicketDTO
    ) throws TicketCategoryNotFoundException, AccountNotFoundException {
        Ticket createdTicket = ticketService.createTicket(openTicketDTO);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all tickets")
    @GetMapping()
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @Operation(summary = "Get a ticket by ID")
    @GetMapping("/{ticketId}")
    public ResponseEntity<Ticket> getTicketById(
            @Parameter(description = "Ticket ID") @PathVariable("ticketId") String ticketId
    ) throws TicketNotFoundException {
        Ticket ticket = ticketService.getTicketById(ticketId);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @Operation(summary = "Resolve a ticket")
    @PutMapping("/{ticketId}/resolve")
    public ResponseEntity<Ticket> resolveTicket(
            @Parameter(description = "Ticket ID") @PathVariable("ticketId") String ticketId,
            @Parameter(description = "Resolution details") @RequestParam("resolve") String resolve,
            @Parameter(description = "User performing the resolution") @RequestParam("user") String user
    ) throws TicketAlreadyResolvedException, TicketNotFoundException {
        Ticket resolvedTicket = ticketService.resolveTicket(ticketId, resolve, user);
        return new ResponseEntity<>(resolvedTicket, HttpStatus.OK);
    }

    @Operation(summary = "Reopen a closed ticket")
    @PutMapping("/{ticketId}/reopen")
    public ResponseEntity<Ticket> reopenTicket(
            @Parameter(description = "Ticket ID") @PathVariable("ticketId") String ticketId,
            @Parameter(description = "Reason for reopening") @RequestParam("reason") String reason,
            @Parameter(description = "User reopening the ticket") @RequestParam("user") String user
    ) throws TicketAlreadyOpenException, TicketNotFoundException {
        Ticket reopenedTicket = ticketService.reopenTicket(ticketId, reason, user);
        return new ResponseEntity<>(reopenedTicket, HttpStatus.OK);
    }

    @Operation(summary = "Add a comment to a ticket")
    @PostMapping("/{ticketId}/comments")
    public ResponseEntity<Ticket> addCommentToTicket(
            @Parameter(description = "Ticket ID") @PathVariable("ticketId") String ticketId,
            @RequestBody CommentDTO comment
    ) throws TicketNotFoundException {
        Ticket ticketWithComment = ticketService.addCommentToTicket(ticketId, comment);
        return new ResponseEntity<>(ticketWithComment, HttpStatus.OK);
    }

    @Operation(summary = "Add an activity to a ticket")
    @PostMapping("/{ticketId}/activities")
    public ResponseEntity<Ticket> addActivityToTicket(
            @Parameter(description = "Ticket ID") @PathVariable("ticketId") String ticketId,
            @RequestBody ActivityDTO activityDto
    )throws TicketNotFoundException {
        Ticket ticketWithActivity = ticketService.addActivityToTicket(ticketId, activityDto);
        return new ResponseEntity<>(ticketWithActivity, HttpStatus.OK);
    }

    @ExceptionHandler({
            DuplicateAccountException.class,
            TicketAlreadyOpenException.class,
            TicketAlreadyResolvedException.class,
            TicketCategoryNotFoundException.class,
            TicketNotFoundException.class,
            AccountNotFoundException.class
    })
    @ApiResponse(responseCode = "400", description = "Bad Request")
    public ResponseEntity<ErrorResponse> handleAccountExceptions(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorDescription(ex.getMessage())
                .errorCode("400")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }



}