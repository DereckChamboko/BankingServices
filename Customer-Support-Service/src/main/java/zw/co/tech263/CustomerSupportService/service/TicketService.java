package zw.co.tech263.CustomerSupportService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final TicketRepository ticketRepository;
    private final CustomerAccountRepository customerAccountRepository;

    private final NotificationService notificationService;


    @Autowired
    public TicketService(TicketRepository ticketRepository,
                         CustomerAccountRepository customerAccountRepository,
                         NotificationService notificationService) {
        this.ticketRepository = ticketRepository;
        this.customerAccountRepository=customerAccountRepository;
        this.notificationService=notificationService;
    }

    public Ticket createTicket(OpenTicketDTO openTicketDTO) throws TicketCategoryNotFoundException, AccountNotFoundException {
        CustomerAccount customerAccount=customerAccountByAccountNumber(openTicketDTO.getCustomerAccountNumber());
        Ticket ticket=Ticket.builder()
                .title(openTicketDTO.getTitle())
                .status(TicketStatus.OPEN)
                .customerAccount(customerAccount)
                .openingDate(Instant.now().toEpochMilli())
                .ticketCategory(getValidTicketCategory(openTicketDTO.getTicketCategory()))
                .description(openTicketDTO.getDescription())
                .build();



        ticket=ticketRepository.save(ticket);
        RabbitMQMessageOut message=RabbitMQMessageOut.builder()
                .messageTittle("New Ticket created-"+ticket.getId())
                .accountNumber(customerAccount.getAccountNumber())
                .message("We have opened a new ticket with tittle "+ticket.getTitle()+" was opened. this ticket will be tracked with ticket ID:"+ticket.getId() )
                .build();
        notificationService.sendNotification(message);
        return ticket;

    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket resolveTicket(String ticketId,String reason,String user) throws TicketAlreadyResolvedException, TicketNotFoundException {

        Ticket ticket = getTicketById(ticketId);
        if (ticket.getStatus() == TicketStatus.RESOLVED) {
            throw new TicketAlreadyResolvedException("Ticket is already resolved.");
        }

        ActivityDTO activity=ActivityDTO.builder()
                .user(user)
                .description("Ticket resolved: "+reason)
                .build();
        ticket=addActivityToTicket(ticketId,activity);
        ticket.setStatus(TicketStatus.RESOLVED);
        return ticketRepository.save(ticket);
    }

    public Ticket reopenTicket(String ticketId,String reason,String user) throws TicketAlreadyOpenException, TicketNotFoundException {
        Ticket ticket = getTicketById(ticketId);
        if (ticket.getStatus() == TicketStatus.OPEN) {
            throw new TicketAlreadyOpenException("Ticket is already open.");
        }

        ActivityDTO activity=ActivityDTO.builder()
                .user(user)
                .description("Ticket re-opened because "+reason)
                .build();
        ticket =addActivityToTicket(ticketId,activity);
        ticket.setStatus(TicketStatus.OPEN);
        return ticketRepository.save(ticket);
    }

    public Ticket addCommentToTicket(String ticketId, CommentDTO comment) throws TicketNotFoundException {

        Ticket ticket = getTicketById(ticketId);
        Comment commentRequest=Comment.builder()
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

        ticket=ticketRepository.save(ticket);
        RabbitMQMessageOut message=RabbitMQMessageOut.builder()
                .messageTittle("Comment added to ticket-"+ticket.getId())
                .accountNumber(ticket.getCustomerAccount().getAccountNumber())
                .message("User "+comment.getUser()+" commented: "+comment.getCommentText()+" on ticket "+ticketId )
                .build();
        notificationService.sendNotification(message);
        return ticket;


    }

    public Ticket addActivityToTicket(String ticketId, ActivityDTO activityDto) throws TicketNotFoundException {

        Activity activity= Activity.builder()
                .activityDate(Instant.now().toEpochMilli())
                .userId(activityDto.getUser())
                .description(activityDto.getDescription())
                .build();

        Ticket ticket = getTicketById(ticketId);
        activity.setActivityDate(Instant.now().toEpochMilli());
        List<Activity> listOfActivitiess = ticket.getActivities();
        if (listOfActivitiess == null) {
            listOfActivitiess = new ArrayList<>();
        }
        listOfActivitiess.add(activity);
        ticket.setActivities(listOfActivitiess);

        ticket= ticketRepository.save(ticket);
        RabbitMQMessageOut message=RabbitMQMessageOut.builder()
                .messageTittle("Activity on ticket-"+ticket.getId())
                .accountNumber(ticket.getCustomerAccount().getAccountNumber())
                .message("User "+activityDto.getUser()+" made an activity : "+ activityDto.getDescription()+" on ticket "+ticketId )
                .build();
        notificationService.sendNotification(message);
        return ticket;
    }

    public Ticket getTicketById(String ticketId) throws TicketNotFoundException {
        return ticketRepository.findById(ticketId).orElseThrow(
                ()->new TicketNotFoundException("Ticket not found.")
        );
    }

    private CustomerAccount customerAccountByAccountNumber(String accountNumber) throws AccountNotFoundException {
        return customerAccountRepository.findByAccountNumber(accountNumber).orElseThrow(
                ()->new AccountNotFoundException("Could not find account "+accountNumber)
        );
    }


    private TicketCategory getValidTicketCategory(String category) throws  TicketCategoryNotFoundException {
        if(category!=null) {
            boolean isValidAccountType = Arrays.stream(TicketCategory.values())
                    .anyMatch(categoryType -> categoryType.name().equals(category));
            if (isValidAccountType) {
                return TicketCategory.valueOf(category);
            }
            throw new TicketCategoryNotFoundException("Invalid ticket category:"+category+ ". expecting any of the following " + Arrays.toString(TicketCategory.values()));

        }
        throw new TicketCategoryNotFoundException("Missing ticket category. Was expecting any of the following " + Arrays.toString(TicketCategory.values()));

    }


}