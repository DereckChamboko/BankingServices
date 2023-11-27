package zw.co.tech263.CustomerSupportService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "customer_accounts")
public class CustomerAccount {
    @Id
    private String id;
    @Indexed(unique = true)
    private String accountNumber;
}
