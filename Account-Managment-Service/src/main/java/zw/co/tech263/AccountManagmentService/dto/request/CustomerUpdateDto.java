package zw.co.tech263.AccountManagmentService.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateDto {

    private  String accountNumber;
    private String firstName;
    private String lastName;
    private String address;
    private String accountType;



}
