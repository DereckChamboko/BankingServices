package zw.co.tech263.CustomerSupportService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "tickets")
public class Ticket {
    @Id
    @Indexed(unique = true)
    private String id;
    private String title;
    private String description;
    private TicketStatus status;
    private CustomerAccount customerAccount;
    private TicketCategory ticketCategory;
    private List<Comment> comments;
    private List<Activity> activities;
    private long openingDate;

}