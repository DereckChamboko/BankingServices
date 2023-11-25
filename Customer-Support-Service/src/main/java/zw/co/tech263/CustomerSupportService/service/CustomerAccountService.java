package zw.co.tech263.CustomerSupportService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zw.co.tech263.CustomerSupportService.dto.request.CustomerAccountDTO;
import zw.co.tech263.CustomerSupportService.exception.DuplicateAccountException;
import zw.co.tech263.CustomerSupportService.model.CustomerAccount;
import zw.co.tech263.CustomerSupportService.repository.CustomerAccountRepository;

@Service
public class CustomerAccountService {
    private final CustomerAccountRepository customerAccountRepository;

    @Autowired
    public CustomerAccountService(CustomerAccountRepository customerAccountRepository) {
        this.customerAccountRepository = customerAccountRepository;
    }

    public CustomerAccount createCustomerAccount(CustomerAccountDTO customerAccount) throws DuplicateAccountException {

        CustomerAccount customerAccountRequest=CustomerAccount.builder()
                .accountNumber(customerAccount.getAccountNumber())
                .build();
        return customerAccountRepository.save(customerAccountRequest);
    }
}
