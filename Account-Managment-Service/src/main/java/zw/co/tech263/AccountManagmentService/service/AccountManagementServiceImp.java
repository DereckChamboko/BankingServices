package zw.co.tech263.AccountManagmentService.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import zw.co.tech263.AccountManagmentService.dto.messaging.TransactionServiceMessage;
import zw.co.tech263.AccountManagmentService.dto.request.CustomerCreationDto;
import zw.co.tech263.AccountManagmentService.dto.request.CustomerUpdateDto;
import zw.co.tech263.AccountManagmentService.dto.StatusUpdate;
import zw.co.tech263.AccountManagmentService.dto.messaging.CustomerSupportAccountMessage;
import zw.co.tech263.AccountManagmentService.dto.messaging.NotificationMessage;
import zw.co.tech263.AccountManagmentService.exception.AccountNotFoundException;
import zw.co.tech263.AccountManagmentService.exception.InvalidAccountTypeException;
import zw.co.tech263.AccountManagmentService.exception.InvalidStatusException;
import zw.co.tech263.AccountManagmentService.model.AccountStatus;
import zw.co.tech263.AccountManagmentService.model.AccountType;
import zw.co.tech263.AccountManagmentService.model.CustomerAccount;
import zw.co.tech263.AccountManagmentService.repository.AccountRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class AccountManagementServiceImp {


    @Autowired
    AccountRepository accountRepository;

    @Autowired
    RestTemplate restTemplate;


    @Autowired
    MessagingService messagingService;


    public CustomerAccount addAccount(CustomerCreationDto account) throws InvalidAccountTypeException, AccountNotFoundException, URISyntaxException {

            CustomerAccount customerAccount=CustomerAccount.builder()
                    .firstName(account.getFirstName())
                    .lastName(account.getLastName())
                    .address(account.getAddress())
                    .accountStatus(AccountStatus.ACTIVE)
                    .accountType(getValidAccountType(account.getAccountType()))
                    .build();
            customerAccount =accountRepository.save(customerAccount);

            NotificationMessage message= NotificationMessage.builder()
                    .accountNumber(customerAccount.getAccountNumber())
                    .messageTittle("New account created-"+customerAccount.getAccountNumber())
                    .message("We are excited to have you onboard")
                    .build();
            messagingService.sendNotification(message);
            messagingService.createCustomerSupportAccount(new CustomerSupportAccountMessage(customerAccount.getAccountNumber()));


            messagingService.createTransactionServiceAccount(new TransactionServiceMessage(customerAccount.getAccountNumber()));

            return customerAccount;

    }

    public ResponseEntity<List<CustomerAccount>> getAllAccount() {
        var allAccounts=accountRepository.findAll();
        return ResponseEntity.ok(allAccounts);
    }


    public CustomerAccount getAccountByAccountNumber(String accountNumber) throws AccountNotFoundException {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Invalid account number: " + accountNumber));
    }


    public CustomerAccount updateAccount(CustomerUpdateDto account) throws InvalidAccountTypeException ,AccountNotFoundException{
        CustomerAccount customerAccount=getAccountByAccountNumber(account.getAccountNumber());
        customerAccount.setAccountType(getValidAccountType(account.getAccountType()));
        customerAccount.setFirstName(Objects.requireNonNullElse(account.getFirstName(), customerAccount.getFirstName()));
        customerAccount.setLastName(Objects.requireNonNullElse(account.getLastName(), customerAccount.getLastName()));
        customerAccount.setAddress(Objects.requireNonNullElse(account.getAddress(), customerAccount.getAddress()));
        return accountRepository.save(customerAccount);

    }

    public CustomerAccount updateAccountStatus(StatusUpdate statusUpdate,String accountNumber) throws InvalidStatusException ,AccountNotFoundException{

            CustomerAccount customerAccount=getAccountByAccountNumber(accountNumber);
            boolean isValidStatus = Arrays.stream(AccountStatus.values())
                    .anyMatch(accountStatus -> accountStatus.name().equals(statusUpdate.getStatus()));
            if (isValidStatus) {
                customerAccount.setAccountStatus(AccountStatus.valueOf(statusUpdate.getStatus()));
            } else {
                throw new InvalidStatusException("Invalid status:" + statusUpdate.getStatus() + ". Was expecting " + Arrays.toString(AccountStatus.values()));
            }
            return accountRepository.save(customerAccount);

    }

    private AccountType getValidAccountType(String accountTypeToValidate) throws InvalidAccountTypeException {
        if(accountTypeToValidate!=null) {
            boolean isValidAccountType = Arrays.stream(AccountType.values())
                    .anyMatch(accountType -> accountType.name().equals(accountTypeToValidate));
            if (isValidAccountType) {
                return AccountType.valueOf(accountTypeToValidate);
            }
            throw new InvalidAccountTypeException("Invalid account type:" + accountTypeToValidate + ". expecting any of the following " + Arrays.toString(AccountType.values()));

        }
        throw new InvalidAccountTypeException("Missing account type .Was expecting any of the following " + Arrays.toString(AccountType.values()));

    }



}
