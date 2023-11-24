package zw.co.tech263.CustomerSupportService.dto;

import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.tech263.CustomerSupportService.model.TicketCategory;
import zw.co.tech263.CustomerSupportService.model.TicketStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenTicketDTO {
    private String customerAccountNumber;
    private String title;
    private String description;
    private String ticketCategory;
}
