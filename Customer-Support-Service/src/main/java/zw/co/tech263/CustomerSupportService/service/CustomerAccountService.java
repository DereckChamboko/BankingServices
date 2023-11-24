package zw.co.tech263.CustomerSupportService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public CustomerAccount createCustomerAccount(CustomerAccount customerAccount) throws DuplicateAccountException {
        return customerAccountRepository.save(customerAccount);
    }
}
