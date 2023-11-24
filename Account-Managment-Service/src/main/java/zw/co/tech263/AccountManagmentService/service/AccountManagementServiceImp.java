package zw.co.tech263.AccountManagmentService.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import zw.co.tech263.AccountManagmentService.dto.Customer;
import zw.co.tech263.AccountManagmentService.dto.StatusUpdate;
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
public class AccountManagementServiceImp implements AccountManagementService{


    @Autowired
    AccountRepository accountRepository;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public CustomerAccount addAccount(Customer account) throws InvalidAccountTypeException, AccountNotFoundException, URISyntaxException {

            CustomerAccount customerAccount=CustomerAccount.builder()
                    .firstName(account.getFirstName())
                    .lastName(account.getLastName())
                    .address(account.getAddress())
                    .accountStatus(AccountStatus.ACTIVE)
                    .accountType(getValidAccountType(account))
                    .build();
            customerAccount =accountRepository.save(customerAccount);
            addAccountToTransactionService(customerAccount.getAccountNumber());
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


    public CustomerAccount updateAccount(Customer account) throws InvalidAccountTypeException ,AccountNotFoundException{
        CustomerAccount customerAccount=getAccountByAccountNumber(account.getAccountNumber());
        customerAccount.setAccountType(getValidAccountType(account));
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

    public void addAccountToTransactionService(String accountNumber) throws AccountNotFoundException, URISyntaxException {

            URI uri = new URI("https://TRANSACTION-PROCESSING-SERVICE/"+"api/v1/accounts");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
            requestParams.add("accountNumber", accountNumber);
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestParams, headers);
            restTemplate.postForObject(uri, requestEntity, Customer.class);

    }

    private AccountType getValidAccountType(Customer account) throws InvalidAccountTypeException {
        if(account.getAccountType()!=null) {
            boolean isValidAccountType = Arrays.stream(AccountType.values())
                    .anyMatch(accountType -> accountType.name().equals(account.getAccountType()));
            if (isValidAccountType) {
                return AccountType.valueOf(account.getAccountType());
            }
            throw new InvalidAccountTypeException("Invalid account type:" + account.getAccountType() + ". expecting any of the following " + Arrays.toString(AccountType.values()));

        }
        throw new InvalidAccountTypeException("Missing account type .Was expecting any of the following " + Arrays.toString(AccountType.values()));

    }



}
