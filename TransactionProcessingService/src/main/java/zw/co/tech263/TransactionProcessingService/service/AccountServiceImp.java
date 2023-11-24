package zw.co.tech263.TransactionProcessingService.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.client.RestTemplate;
import zw.co.tech263.TransactionProcessingService.dto.AccountStatus;
import zw.co.tech263.TransactionProcessingService.dto.CustomerAccount;
import zw.co.tech263.TransactionProcessingService.exception.AccountNotActiveException;
import zw.co.tech263.TransactionProcessingService.exception.AccountNotFoundException;
import zw.co.tech263.TransactionProcessingService.exception.InsufficientFundsException;
import zw.co.tech263.TransactionProcessingService.exception.TransactionsNotFoundException;
import zw.co.tech263.TransactionProcessingService.model.Account;
import zw.co.tech263.TransactionProcessingService.model.Transaction;
import zw.co.tech263.TransactionProcessingService.model.TransactionType;
import zw.co.tech263.TransactionProcessingService.repository.AccountRepository;
import zw.co.tech263.TransactionProcessingService.repository.TransactionRepository;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;


@Service
@Transactional
public class AccountServiceImp {


    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private final RestTemplate restTemplate;

    @Autowired
    public AccountServiceImp(AccountRepository accountRepository, TransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate=restTemplate;

    }

    public void deposit(String accountNumber, BigDecimal amount,String decription) throws AccountNotFoundException, AccountNotActiveException {

        Account account =findAccount(accountNumber);
        validateAccountIsActive(accountNumber);

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        createTransaction(account.getAccountNumber(), TransactionType.DEPOSIT, amount,decription);
    }

    public void withdraw(String accountNumber, BigDecimal amount,String description) throws AccountNotFoundException, InsufficientFundsException, AccountNotActiveException {

        Account account =findAccount(accountNumber);

        validateAccountIsActive(accountNumber);

        BigDecimal currentBalance = account.getBalance();
        if (currentBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the account");
        }

        BigDecimal newBalance = currentBalance.subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        createTransaction(account.getAccountNumber(), TransactionType.WITHDRAWAL, amount, description);
    }

    public BigDecimal getBalance(String accountNumber) throws AccountNotFoundException {
        Account account =findAccount(accountNumber);
        return account.getBalance();
    }

    public List<Transaction> getLastTransactions(String accountNumber,int lastTransactionCount) throws TransactionsNotFoundException {

       return transactionRepository.findLast10ByAccountNumber(accountNumber,Pageable.ofSize(10))
               .orElseThrow(()-> new TransactionsNotFoundException("No transactions for account "+accountNumber+" were found"));
    }

    public void transferFunds(String accountNumber, String destinationAccountNumber, BigDecimal amount,String description) throws InsufficientFundsException, AccountNotFoundException {


        Account sourceAccount =findAccount(accountNumber);
        Account destinationAccount =findAccount(destinationAccountNumber);


        BigDecimal sourceBalance = sourceAccount.getBalance();

        if (sourceBalance.subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the source account");
        }
        BigDecimal destinationBalance = destinationAccount.getBalance();
        BigDecimal sourceNewBalance = sourceBalance.subtract(amount);
        BigDecimal destinationNewBalance = destinationBalance.add(amount);
        sourceAccount.setBalance(sourceNewBalance);
        destinationAccount.setBalance(destinationNewBalance);


        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        createTransaction(sourceAccount.getAccountNumber(), TransactionType.WITHDRAWAL, amount, "Transfer to Acc:"+destinationAccount+" with description "+description);
        createTransaction(destinationAccount.getAccountNumber(), TransactionType.DEPOSIT, amount, "Transfer from Acc:"+sourceAccount+" with description "+description);
    }

    private void createTransaction(String account, TransactionType transactionType, BigDecimal amount,String description) {

        Transaction transaction = new Transaction();
        transaction.setCreatedAt(Instant.now().toEpochMilli());
        transaction.setDescription(description);
        transaction.setAccountNumber(account);

        if(transactionType==TransactionType.DEPOSIT){
            transaction.setDr(amount);
        }
        if(transactionType==TransactionType.WITHDRAWAL){
            transaction.setCr(amount);
        }
        transactionRepository.save(transaction);
    }

    public Account addNewCustomerAccount(String accountNumber){
        Account account=Account.builder()
                .balance(BigDecimal.ZERO)
                .accountNumber(accountNumber)
                .build();

        return accountRepository.save(account);

    }

    public CustomerAccount getAccountDetails(String accountNumber) throws AccountNotFoundException {

        try {
            URI uri = new URI("https://ACCOUNT-MANAGEMENT-SERVICE/"+"api/v1/accounts/"+accountNumber);
            var customerAccountResponse=restTemplate.getForEntity(uri, CustomerAccount.class);
            if(customerAccountResponse.getStatusCode()== HttpStatusCode.valueOf(404)){
                throw new AccountNotFoundException("Account "+accountNumber+" does not exists");
            }
            return customerAccountResponse.getBody();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


    }

    private void validateAccountIsActive(String accountNumber) throws AccountNotFoundException, AccountNotActiveException {
        CustomerAccount customerAccount=getAccountDetails(accountNumber);
        if(!customerAccount.getAccountStatus().equals(AccountStatus.ACTIVE)){
            throw new AccountNotActiveException("Account "+accountNumber+" is "+customerAccount.getAccountStatus().toString());
        }
    }

    private Account findAccount(String accountNumber) throws AccountNotFoundException {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with Account number: " + accountNumber));
    }

}
