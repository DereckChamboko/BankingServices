package zw.co.tech263.TransactionProcessingService.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.client.RestTemplate;
import zw.co.tech263.TransactionProcessingService.dto.CustomerAccount;
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

    public void deposit(String accountNumber, BigDecimal amount,String decription) throws AccountNotFoundException {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with Account number: " + accountNumber));

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);


        createTransaction(account.getAccountNumber(), TransactionType.DEPOSIT, amount,decription);
    }

    public void withdraw(String accountId, BigDecimal amount,String description) throws AccountNotFoundException, InsufficientFundsException {
        Account account = accountRepository.findByAccountNumber(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

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
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountNumber));

        return account.getBalance();
    }

    public List<Transaction> getLastTransactions(String accountNumber,int lastTransactionCount) throws TransactionsNotFoundException {

       return transactionRepository.findLast10ByAccountNumber(accountNumber,Pageable.ofSize(10))
               .orElseThrow(()-> new TransactionsNotFoundException("No transactions for account "+accountNumber+" were found"));
    }

    public void transferFunds(String accountNumber, String destinationAccountNumber, BigDecimal amount,String description) throws InsufficientFundsException, AccountNotFoundException {
        System.out.println("1");
        Account sourceAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Source account not found with account number: " + accountNumber));
System.out.println("2");
        Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Destination account not found with account number: " + destinationAccountNumber));
        System.out.println("3");
        BigDecimal sourceBalance = sourceAccount.getBalance();
        System.out.println("4");
        if (sourceBalance.subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the source account");
        }
        System.out.println("5");
        BigDecimal destinationBalance = destinationAccount.getBalance();
        System.out.println("6");
        BigDecimal sourceNewBalance = sourceBalance.subtract(amount);
        System.out.println("7");
        BigDecimal destinationNewBalance = destinationBalance.add(amount);
        System.out.println("8");
        sourceAccount.setBalance(sourceNewBalance);
        System.out.println("9");
        destinationAccount.setBalance(destinationNewBalance);

        System.out.println("10");
        accountRepository.save(sourceAccount);
        System.out.println("11");
        accountRepository.save(destinationAccount);
        System.out.println("12");
        createTransaction(sourceAccount.getAccountNumber(), TransactionType.WITHDRAWAL, amount, "Transfer to Acc:"+destinationAccount+" with description "+description);
        System.out.println("13");
        createTransaction(destinationAccount.getAccountNumber(), TransactionType.DEPOSIT, amount, "Transfer from Acc:"+sourceAccount+" with description "+description);
        System.out.println("14");
    }

    private void createTransaction(String account, TransactionType transactionType, BigDecimal amount,String description) {
        System.out.println("a");
        Transaction transaction = new Transaction();
        transaction.setCreatedAt(Instant.now().toEpochMilli());
        transaction.setDescription(description);
        transaction.setAccountNumber(account);
        System.out.println("b");
        if(transactionType==TransactionType.DEPOSIT){
            transaction.setDr(amount);
            System.out.println("c");
        }
        if(transactionType==TransactionType.WITHDRAWAL){
            transaction.setCr(amount);
            System.out.println("d");
        }
        System.out.println("e");
        transactionRepository.save(transaction);
        System.out.println("f");
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
            URI uri = new URI("http://localhost:8763/api/v1/accounts/"+accountNumber);
            var customerAccountResponse=restTemplate.getForEntity(uri, CustomerAccount.class);
            if(customerAccountResponse.getStatusCode()== HttpStatusCode.valueOf(404)){
                throw new AccountNotFoundException("Account "+accountNumber+" does not exists");
            }
            return customerAccountResponse.getBody();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


    }
}
