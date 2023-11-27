package zw.co.tech263.transaction_processing_service.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.client.RestTemplate;
import zw.co.tech263.transaction_processing_service.dto.AccountStatus;
import zw.co.tech263.transaction_processing_service.dto.CustomerAccount;
import zw.co.tech263.transaction_processing_service.exception.*;
import zw.co.tech263.transaction_processing_service.model.Account;
import zw.co.tech263.transaction_processing_service.model.Transaction;
import zw.co.tech263.transaction_processing_service.model.TransactionType;
import zw.co.tech263.transaction_processing_service.repository.AccountRepository;
import zw.co.tech263.transaction_processing_service.repository.TransactionRepository;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@EnableCaching
public class AccountServiceImp implements AccountService {


    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;
    private final CacheManager cacheManager;
    private final Logger logger = LoggerFactory.getLogger(AccountServiceImp.class);

    @Autowired
    public AccountServiceImp(AccountRepository accountRepository, TransactionRepository transactionRepository, RestTemplate restTemplate, CacheManager cacheManager) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
        this.cacheManager = cacheManager;
    }



    public void deposit(String accountNumber, BigDecimal amount,String description) throws AccountNotFoundException, AccountNotActiveException, URISyntaxException, InvalidAmountExeption {

        checkIsPositiveAmount(amount);
        Account account =findAccount(accountNumber);
        validateAccountIsActive(accountNumber);
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        account=accountRepository.save(account);
        createTransaction(account.getAccountNumber(), TransactionType.DEPOSIT, amount,description);
        updateBalanceCache(account);
        logger.info("Deposit successful for account: {}. Amount: {}. Description: {}", accountNumber, amount, description);
    }


    public void withdraw(String accountNumber, BigDecimal amount,String description) throws AccountNotFoundException, InsufficientFundsException, AccountNotActiveException, URISyntaxException, InvalidAmountExeption {
        checkIsPositiveAmount(amount);
        Account account =findAccount(accountNumber);
        validateAccountIsActive(accountNumber);
        BigDecimal currentBalance = account.getBalance();
        if (currentBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the account");
        }
        BigDecimal newBalance = currentBalance.subtract(amount);
        account.setBalance(newBalance);
        account=accountRepository.save(account);
        createTransaction(account.getAccountNumber(), TransactionType.WITHDRAWAL, amount, description);
        updateBalanceCache(account);
        logger.info("Withdrawal successful for account: {}. Amount: {}. Description: {}", accountNumber, amount, description);
    }



    @Cacheable(value = "balanceCache", key = "'balance_' + #accountNumber")
    public BigDecimal getBalance(String accountNumber) throws AccountNotFoundException {
        Account account = findAccount(accountNumber);
        BigDecimal balance = account.getBalance();
        logger.info("Retrieved balance for account {}: {}", accountNumber, balance);
        return balance;
    }





    public List<Transaction> getLastTransactions(String accountNumber, int lastTransactionCount) throws TransactionsNotFoundException {
        List<Transaction> transactions = transactionRepository.findLast10ByAccountNumber(accountNumber, Pageable.ofSize(lastTransactionCount))
                .orElseThrow(() -> new TransactionsNotFoundException("No transactions for account " + accountNumber + " were found"));

        logger.info("Retrieved last {} transactions for account {}: {}", lastTransactionCount, accountNumber, transactions);
        return transactions;
    }

    public void transferFunds(String accountNumber, String destinationAccountNumber, BigDecimal amount, String description) throws InsufficientFundsException, AccountNotFoundException, InvalidAmountExeption {
        Account sourceAccount = findAccount(accountNumber);
        Account destinationAccount = findAccount(destinationAccountNumber);
        checkIsPositiveAmount(amount);

        BigDecimal sourceBalance = sourceAccount.getBalance();
        BigDecimal destinationBalance = destinationAccount.getBalance();

        BigDecimal sourceNewBalance = sourceBalance.subtract(amount);
        BigDecimal destinationNewBalance = destinationBalance.add(amount);

        if (sourceNewBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the source account");
        }

        sourceAccount.setBalance(sourceNewBalance);
        destinationAccount.setBalance(destinationNewBalance);

        sourceAccount = accountRepository.save(sourceAccount);
        destinationAccount = accountRepository.save(destinationAccount);

        String withdrawalTransactionDescription = "Transfer to Acc: " + destinationAccountNumber + " with description " + description;
        String depositTransactionDescription = "Transfer from Acc: " + accountNumber + " with description " + description;

        createTransaction(sourceAccount.getAccountNumber(), TransactionType.WITHDRAWAL, amount, withdrawalTransactionDescription);
        createTransaction(destinationAccount.getAccountNumber(), TransactionType.DEPOSIT, amount, depositTransactionDescription);

        updateBalanceCache(sourceAccount);
        updateBalanceCache(destinationAccount);

        logger.info("Funds transferred from account {} to account {}. Amount: {}. Description: {}", accountNumber, destinationAccountNumber, amount, description);
    }

    private void createTransaction(String accountNumber, TransactionType transactionType, BigDecimal amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setCreatedAt(Instant.now().toEpochMilli());
        transaction.setDescription(description);
        transaction.setAccountNumber(accountNumber);

        if (transactionType == TransactionType.DEPOSIT) {
            transaction.setDr(amount);
        } else if (transactionType == TransactionType.WITHDRAWAL) {
            transaction.setCr(amount);
        }

        transactionRepository.save(transaction);

        logger.info("Transaction created for account: {}. Type: {}. Amount: {}. Description: {}", accountNumber, transactionType, amount, description);
    }


    public Account addNewCustomerAccount(String accountNumber) {
        logger.info("Adding new customer account with accountNumber: {}", accountNumber);

        Account account = Account.builder()
                .balance(BigDecimal.ZERO)
                .accountNumber(accountNumber)
                .build();

        Account savedAccount = accountRepository.save(account);
        logger.debug("New customer account added successfully. Account ID: {}", savedAccount.getAccountId());

        return savedAccount;
    }

    public CustomerAccount getAccountDetails(String accountNumber) throws AccountNotFoundException, URISyntaxException {
        logger.info("Fetching account details for accountNumber: {}", accountNumber);
            URI uri = new URI("https://ACCOUNT-MANAGEMENT-SERVICE/" + "api/v1/accounts/" + accountNumber);
            logger.debug("Sending request to Account Management Service: {}", uri);

            var customerAccountResponse = restTemplate.getForEntity(uri, CustomerAccount.class);
            if (customerAccountResponse.getStatusCode() == HttpStatusCode.valueOf(404) ){
                String errorMessage = "Account " + accountNumber + " does not exist";
                logger.warn(errorMessage);
                throw new AccountNotFoundException(errorMessage);
            }

            CustomerAccount customerAccount = customerAccountResponse.getBody();
            logger.info("Account details retrieved successfully for accountNumber: {}", accountNumber);
            return customerAccount;

    }




    private void validateAccountIsActive(String accountNumber) throws AccountNotFoundException, AccountNotActiveException, URISyntaxException {
        logger.info("Validating account is active for accountNumber: {}", accountNumber);

            CustomerAccount customerAccount = getAccountDetails(accountNumber);
            logger.debug("Retrieved account details for accountNumber: {}. Account status: {}", accountNumber, customerAccount.getAccountStatus());

            if (!customerAccount.getAccountStatus().equals(AccountStatus.ACTIVE)) {
                String errorMessage = "Account " + accountNumber + " is " + customerAccount.getAccountStatus().toString();
                logger.warn(errorMessage);
                throw new AccountNotActiveException(errorMessage);
            }

    }

    @Cacheable(value = "accountCache", key = "'account_' + #accountNumber")
    private Account findAccount(String accountNumber) throws AccountNotFoundException {
        logger.info("Finding account with accountNumber: {}", accountNumber);
            Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                logger.debug("Account found with accountNumber: {}", accountNumber);
                return account;
            } else {
                String errorMessage = "Account not found with Account number: " + accountNumber;
                logger.warn(errorMessage);
                throw new AccountNotFoundException(errorMessage);
            }
    }




    private void updateBalanceCache(Account account) {
        logger.info("Updating balance cache for account: {}", account.getAccountNumber());

        Cache balanceCache = cacheManager.getCache("balanceCache");
        if (balanceCache != null) {
            balanceCache.put("balance_" + account.getAccountNumber(), account.getBalance());
            logger.debug("Balance cache updated successfully for account: {}", account.getAccountNumber());
        } else {
            logger.warn("balanceCache not found. Unable to update balance cache.");
        }
    }

    private void checkIsPositiveAmount(BigDecimal amount) throws InvalidAmountExeption {
        logger.info("Checking if amount {} is positive",amount);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Amount is not positive, InvalidAmountExeption will be thrown");
            throw new InvalidAmountExeption("Only positive amounts are allowed, "+amount+" will be rejected");
        }
    }



}
