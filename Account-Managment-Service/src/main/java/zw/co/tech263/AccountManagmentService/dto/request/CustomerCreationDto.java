package zw.co.tech263.AccountManagmentService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreationDto {


    private String firstName;
    private String lastName;
    private String address;
    private String accountType;
}