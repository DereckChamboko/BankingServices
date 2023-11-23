package zw.co.tech263.AccountManagmentService.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import zw.co.tech263.AccountManagmentService.dto.Customer;
import zw.co.tech263.AccountManagmentService.dto.StatusUpdate;
import zw.co.tech263.AccountManagmentService.exception.AccountNotFoundException;
import zw.co.tech263.AccountManagmentService.exception.InvalidAccountTypeException;
import zw.co.tech263.AccountManagmentService.exception.InvalidStatusException;
import zw.co.tech263.AccountManagmentService.model.AccountStatus;
import zw.co.tech263.AccountManagmentService.model.AccountType;
import zw.co.tech263.AccountManagmentService.model.CustomerAccount;
import zw.co.tech263.AccountManagmentService.repository.AccountRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class AccountManagementServiceImp implements AccountManagementService{


    @Autowired
    AccountRepository accountRepository;
    @Override
    public CustomerAccount addAccount(Customer account) throws InvalidAccountTypeException {

        boolean isValidAccountType = Arrays.stream(AccountType.values())
                .anyMatch(accountType -> accountType.name().equals(account.getAccountType()));
        if(isValidAccountType){
            CustomerAccount customerAccount=CustomerAccount.builder()
                    .firstName(account.getFirstName())
                    .lastName(account.getLastName())
                    .address(account.getAddress())
                    .accountType(AccountType.valueOf(account.getAccountType()))
                    .build();
            return accountRepository.save(customerAccount);
        }else{
            throw new InvalidAccountTypeException("Invalid account type:"+account.getAccountType()+". Was expecting "+Arrays.toString(AccountType.values()));
        }

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
        if(account.getAccountType()!=null) {
            boolean isValidAccountType = Arrays.stream(AccountType.values())
                    .anyMatch(accountType -> accountType.name().equals(account.getAccountType()));
            if (isValidAccountType) {
                customerAccount.setAccountType(AccountType.valueOf(account.getAccountType()));
            } else {
                throw new InvalidAccountTypeException("Invalid account type:" + account.getAccountType() + ". Was expecting " + Arrays.toString(AccountType.values()));
            }
        }


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

}
