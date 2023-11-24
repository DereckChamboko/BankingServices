package zw.co.tech263.CustomerSupportService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zw.co.tech263.CustomerSupportService.dto.OpenTicketDTO;
import zw.co.tech263.CustomerSupportService.exception.*;
import zw.co.tech263.CustomerSupportService.model.*;
import zw.co.tech263.CustomerSupportService.repository.CustomerAccountRepository;
import zw.co.tech263.CustomerSupportService.repository.TicketRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final CustomerAccountRepository customerAccountRepository;


    @Autowired
    public TicketService(TicketRepository ticketRepository,CustomerAccountRepository customerAccountRepository) {
        this.ticketRepository = ticketRepository;
        this.customerAccountRepository=customerAccountRepository;
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
        return ticketRepository.save(ticket);

    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket resolveTicket(String ticketId,String reason,String user) throws TicketAlreadyResolvedException, TicketNotFoundException {

        Ticket ticket = getTicketById(ticketId);
        if (ticket.getStatus() == TicketStatus.RESOLVED) {
            throw new TicketAlreadyResolvedException("Ticket is already resolved.");
        }

        Activity activity=Activity.builder()
                .userId(user)
                .activityDate(Instant.now().toEpochMilli())
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

        Activity activity=Activity.builder()
                .userId(user)
                .activityDate(Instant.now().toEpochMilli())
                .description("Ticket re-opened because "+reason)
                .build();
        ticket =addActivityToTicket(ticketId,activity);
        ticket.setStatus(TicketStatus.OPEN);
        return ticketRepository.save(ticket);
    }

    public Ticket addCommentToTicket(String ticketId, Comment comment) throws TicketNotFoundException {

        Ticket ticket = getTicketById(ticketId);
        comment.setCommentDate(Instant.now().toEpochMilli());

        List<Comment> listOfComments = ticket.getComments();
        if (listOfComments == null) {
            listOfComments = new ArrayList<>();
        }
        listOfComments.add(comment);
        ticket.setComments(listOfComments);

        return ticketRepository.save(ticket);
    }

    public Ticket addActivityToTicket(String ticketId, Activity activity) throws TicketNotFoundException {

        Ticket ticket = getTicketById(ticketId);
        activity.setActivityDate(Instant.now().toEpochMilli());
        List<Activity> listOfActivitiess = ticket.getActivities();
        if (listOfActivitiess == null) {
            listOfActivitiess = new ArrayList<>();
        }
        listOfActivitiess.add(activity);
        ticket.setActivities(listOfActivitiess);

        return ticketRepository.save(ticket);
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