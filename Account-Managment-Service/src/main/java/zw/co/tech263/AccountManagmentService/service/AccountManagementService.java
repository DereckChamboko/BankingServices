package zw.co.tech263.AccountManagmentService.service;



import org.springframework.http.ResponseEntity;
import zw.co.tech263.AccountManagmentService.dto.Customer;
import zw.co.tech263.AccountManagmentService.exception.InvalidAccountTypeException;
import zw.co.tech263.AccountManagmentService.model.CustomerAccount;

import java.util.List;


public interface AccountManagementService {

    CustomerAccount addAccount(Customer account) throws InvalidAccountTypeException;
    ResponseEntity<List<CustomerAccount>> getAllAccount();
}
