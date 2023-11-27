package zw.co.tech263.CustomerSupportService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zw.co.tech263.CustomerSupportService.dto.message.RabbitMQMessageOut;
import zw.co.tech263.CustomerSupportService.dto.request.ActivityDTO;
import zw.co.tech263.CustomerSupportService.dto.request.CommentDTO;
import zw.co.tech263.CustomerSupportService.dto.request.OpenTicketDTO;
import zw.co.tech263.CustomerSupportService.exception.*;
import zw.co.tech263.CustomerSupportService.model.*;
import zw.co.tech263.CustomerSupportService.repository.CustomerAccountRepository;
import zw.co.tech263.CustomerSupportService.repository.TicketRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class TicketService {
    private static final Logger logger = LogManager.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final NotificationService notificationService;

    @Autowired
    public TicketService(TicketRepository ticketRepository,
                         CustomerAccountRepository customerAccountRepository,
                         NotificationService notificationService) {
        this.ticketRepository = ticketRepository;
        this.customerAccountRepository = customerAccountRepository;
        this.notificationService = notificationService;
    }

    public Ticket createTicket(OpenTicketDTO openTicketDTO) throws TicketCategoryNotFoundException, AccountNotFoundException {
        logger.info("Creating a new ticket");

        CustomerAccount customerAccount = customerAccountByAccountNumber(openTicketDTO.getCustomerAccountNumber());
        Ticket ticket = Ticket.builder()
                .title(openTicketDTO.getTitle())
                .status(TicketStatus.OPEN)
                .customerAccount(customerAccount)
                .openingDate(Instant.now().toEpochMilli())
                .ticketCategory(getValidTicketCategory(openTicketDTO.getTicketCategory()))
                .description(openTicketDTO.getDescription())
                .build();

        ticket = ticketRepository.save(ticket);

        RabbitMQMessageOut message = RabbitMQMessageOut.builder()
                .messageTittle("New Ticket created-" + ticket.getId())
                .accountNumber(customerAccount.getAccountNumber())
                .message("We have opened a new ticket with tittle " + ticket.getTitle() + " was opened. this ticket will be tracked with ticket ID:" + ticket.getId())
                .build();
        notificationService.sendNotification(message);

        logger.info("New ticket created successfully. Ticket ID: {}", ticket.getId());

        return ticket;
    }

    public List<Ticket> getAllTickets() {
        logger.info("Getting all tickets");
        return ticketRepository.findAll();
    }

    public Ticket resolveTicket(String ticketId, String reason, String user) throws TicketAlreadyResolvedException, TicketNotFoundException {
        logger.info("Resolving ticket with ID: {}", ticketId);

        Ticket ticket = getTicketById(ticketId);

        if (ticket.getStatus() == TicketStatus.RESOLVED) {
            throw new TicketAlreadyResolvedException("Ticket is already resolved.");
        }

        ActivityDTO activity = ActivityDTO.builder()
                .user(user)
                .description("Ticket resolved: " + reason)
                .build();

        ticket = addActivityToTicket(ticketId, activity);
        ticket.setStatus(TicketStatus.RESOLVED);

        logger.info("Ticket resolved successfully. Ticket ID: {}", ticket.getId());

        return ticketRepository.save(ticket);
    }

    public Ticket reopenTicket(String ticketId, String reason, String user) throws TicketAlreadyOpenException, TicketNotFoundException {
        logger.info("Reopening ticket with ID: {}", ticketId);

        Ticket ticket = getTicketById(ticketId);

        if (ticket.getStatus() == TicketStatus.OPEN) {
            throw new TicketAlreadyOpenException("Ticket is already open.");
        }

        ActivityDTO activity = ActivityDTO.builder()
                .user(user)
                .description("Ticket re-opened because " + reason)
                .build();

        ticket = addActivityToTicket(ticketId, activity);
        ticket.setStatus(TicketStatus.OPEN);

        logger.info("Ticket reopened successfully. Ticket ID: {}", ticket.getId());

        return ticketRepository.save(ticket);
    }

    public Ticket addCommentToTicket(String ticketId, CommentDTO comment) throws TicketNotFoundException {
        logger.info("Adding comment to ticket with ID: {}", ticketId);

        Ticket ticket = getTicketById(ticketId);

        Comment commentRequest = Comment.builder()
                .userId(comment.getUser())
                .commentDate(Instant.now().toEpochMilli())
                .commentText(comment.getCommentText())
                .build();

        List<Comment> listOfComments = ticket.getComments();
        if (listOfComments == null) {
            listOfComments = new ArrayList<>();
        }
        listOfComments.add(commentRequest);
        ticket.setComments(listOfComments);

        ticket = ticketRepository.save(ticket);

        RabbitMQMessageOut message = RabbitMQMessageOut.builder()
                .messageTittle("Comment added to ticket-" + ticket.getId())
                .accountNumber(ticket.getCustomerAccount().getAccountNumber())
                .message("User " + comment.getUser() + " commented: " + comment.getCommentText() + " on ticket " + ticketId)
                .build();
        notificationService.sendNotification(message);

        logger.info("Comment added to ticket successfully. Ticket ID: {}", ticket.getId());

        return ticket;
    }

    public Ticket addActivityToTicket(String ticketId, ActivityDTO activityDto) throws TicketNotFoundException {
        logger.info("Adding activity to ticket with ID: {}", ticketId);

        Activity activity = Activity.builder()
                .activityDate(Instant.now().toEpochMilli())
                .userId(activityDto.getUser())
                .description(activityDto.getDescription())
                .build();

        Ticket ticket = getTicketById(ticketId);
        activity.setActivityDate(Instant.now().toEpochMilli());
        List<Activity> listOfActivities = ticket.getActivities();
        if (listOfActivities == null) {
            listOfActivities = new ArrayList<>();
        }
        listOfActivities.add(activity);
        ticket.setActivities(listOfActivities);

        ticket = ticketRepository.save(ticket);

        RabbitMQMessageOut message = RabbitMQMessageOut.builder()
                .messageTittle("Activity on ticket-" + ticket.getId())
                .accountNumber(ticket.getCustomerAccount().getAccountNumber())
                .message("User " + activityDto.getUser() + " made an activity: " + activityDto.getDescription() + " on ticket " + ticketId)
                .build();
        notificationService.sendNotification(message);

        logger.info("Activity added to ticket successfully. Ticket ID: {}", ticket.getId());

        return ticket;
    }

    public Ticket getTicketById(String ticketId) throws TicketNotFoundException {
        logger.info("Getting ticket by ID: {}", ticketId);

        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found."));
    }

    private CustomerAccount customerAccountByAccountNumber(String accountNumber) throws AccountNotFoundException {
        logger.info("Getting customer account by account number: {}", accountNumber);

        return customerAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Could not find account " + accountNumber));
    }

    private TicketCategory getValidTicketCategory(String category) throws TicketCategoryNotFoundException {
        logger.info("Validating ticket category: {}", category);

        if (category != null) {
            boolean isValidAccountType = Arrays.stream(TicketCategory.values())
                    .anyMatch(categoryType -> categoryType.name().equals(category));
            if (isValidAccountType) {
                return TicketCategory.valueOf(category);
            }
            throw new TicketCategoryNotFoundException("Invalid ticket category: " + category + ". Expecting any of the following: " + Arrays.toString(TicketCategory.values()));
        }

        throw new TicketCategoryNotFoundException("Missing ticket category. Was expecting any of the following: " + Arrays.toString(TicketCategory.values()));
    }
}