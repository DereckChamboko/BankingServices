package zw.co.tech263.AccountManagmentService.service;



import org.springframework.http.ResponseEntity;
import zw.co.tech263.AccountManagmentService.dto.request.CustomerUpdateDto;
import zw.co.tech263.AccountManagmentService.exception.AccountNotFoundException;
import zw.co.tech263.AccountManagmentService.exception.InvalidAccountTypeException;
import zw.co.tech263.AccountManagmentService.model.CustomerAccount;

import java.net.URISyntaxException;
import java.util.List;


public interface AccountManagementService {

    CustomerAccount addAccount(CustomerUpdateDto account) throws InvalidAccountTypeException, AccountNotFoundException, URISyntaxException;
    ResponseEntity<List<CustomerAccount>> getAllAccount();
}
