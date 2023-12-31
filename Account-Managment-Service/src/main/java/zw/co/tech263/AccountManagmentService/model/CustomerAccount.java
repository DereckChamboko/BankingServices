package zw.co.tech263.AccountManagmentService.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class CustomerAccount {




    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;
    private final String accountNumber= getNewAccountNumber();
    private String firstName;
    private String lastName;
    private String address;
    private AccountType accountType;


    private AccountStatus accountStatus;

    private final long accountCreated= Instant.now().toEpochMilli();

    private static String getNewAccountNumber() {
        Double randomVal1 = Math.random() * 10;
        Double randomVal2 = Math.random() * 10;

        return String.valueOf(randomVal1.intValue()) +
                Instant.now().getEpochSecond() +
                String.valueOf(randomVal2.intValue());

    }
}


