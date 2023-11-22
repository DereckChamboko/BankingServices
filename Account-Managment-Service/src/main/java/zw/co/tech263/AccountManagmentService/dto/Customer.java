package zw.co.tech263.AccountManagmentService.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.tech263.AccountManagmentService.model.AccountStatus;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {


    private long id;
    private  String accountNumber;
    private String firstName;
    private String LastName;
    private String address;
    private String accountType;
    private BigDecimal accountBalance;
    private AccountStatus accountStatus;
    private long AccountCreated;
}
