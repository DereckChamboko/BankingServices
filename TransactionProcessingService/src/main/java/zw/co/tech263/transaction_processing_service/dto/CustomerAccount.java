package zw.co.tech263.transaction_processing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAccount {


    private  String accountNumber;
    private String firstName;
    private String lastName;
    private String address;
    private AccountType accountType;

    private BigDecimal accountBalance;

    private AccountStatus accountStatus;

    private long accountCreated;
}
