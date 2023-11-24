package zw.co.tech263.CustomerSupportService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



public enum TicketCategory {
TRANSACTION_DISPUTE,
    CARD_REPLACEMENT,
    LOAN_APPLICATION,
    FRADULANT_ACTIVITY_REPORT,
    CHANGE_OF_ADDRESS,


    }
