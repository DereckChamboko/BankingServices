package zw.co.tech263.CustomerSupportService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zw.co.tech263.CustomerSupportService.dto.request.CustomerAccountDTO;
import zw.co.tech263.CustomerSupportService.exception.DuplicateAccountException;
import zw.co.tech263.CustomerSupportService.model.CustomerAccount;
import zw.co.tech263.CustomerSupportService.repository.CustomerAccountRepository;

@Service
public class CustomerAccountService {
    private static final Logger logger = LogManager.getLogger(CustomerAccountService.class);

    private final CustomerAccountRepository customerAccountRepository;

    @Autowired
    public CustomerAccountService(CustomerAccountRepository customerAccountRepository) {
        this.customerAccountRepository = customerAccountRepository;
    }

    public CustomerAccount createCustomerAccount(CustomerAccountDTO customerAccount) throws DuplicateAccountException {
        logger.info("Creating customer account");

        CustomerAccount customerAccountRequest = CustomerAccount.builder()
                .accountNumber(customerAccount.getAccountNumber())
                .build();

        customerAccountRequest = customerAccountRepository.save(customerAccountRequest);

        logger.info("Customer account created successfully. Account number: {}", customerAccountRequest.getAccountNumber());
        return customerAccountRequest;
    }
}