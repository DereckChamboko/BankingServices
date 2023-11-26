package zw.co.tech263.TransactionProcessingService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.tech263.TransactionProcessingService.exception.AccountNotActiveException;
import zw.co.tech263.TransactionProcessingService.exception.AccountNotFoundException;
import zw.co.tech263.TransactionProcessingService.exception.InsufficientFundsException;
import zw.co.tech263.TransactionProcessingService.exception.TransactionsNotFoundException;
import zw.co.tech263.TransactionProcessingService.model.Account;
import zw.co.tech263.TransactionProcessingService.model.Transaction;
import zw.co.tech263.TransactionProcessingService.service.AccountServiceImp;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/v1/accounts")
public class AccountController {
    private final AccountServiceImp accountService;

    @Autowired
    public AccountController(AccountServiceImp accountService) {
        this.accountService = accountService;
    }


    @PostMapping("/{accountNumber}/deposit")
    public void deposit(@PathVariable("accountNumber") String accountNumber, @RequestParam("amount") BigDecimal amount,@RequestParam("purpose") String description) throws AccountNotFoundException, AccountNotActiveException {
        accountService.deposit(accountNumber, amount,description);
    }

    @PostMapping("/{accountNumber}/withdraw")
    public void withdraw(@PathVariable("accountNumber") String accountNumber, @RequestParam("amount") BigDecimal amount,@RequestParam("purpose") String description) throws InsufficientFundsException, AccountNotFoundException, AccountNotActiveException {
        accountService.withdraw(accountNumber, amount,description);
    }

    @GetMapping("/{accountNumber}/balance")
    public BigDecimal getBalance(@PathVariable("accountNumber") String accountNumber) throws AccountNotFoundException {
        return accountService.getBalance(accountNumber);
    }

    @GetMapping("/{accountNumber}/transactions")
    public List<Transaction> getTransactions(@PathVariable("accountNumber") String accountNumber, @RequestParam("number_of_transactions") int transactionCount) throws AccountNotFoundException, TransactionsNotFoundException {
        return accountService.getLastTransactions(accountNumber,transactionCount);
    }

    @PostMapping("/{accountNumber}/transfer")
    public void transferFunds(
            @PathVariable("accountNumber") String accountNumber,
            @RequestParam("destinationaccountNumber") String destinationaccountNumber,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("purpose") String description) throws InsufficientFundsException, AccountNotFoundException {
        accountService.transferFunds(accountNumber, destinationaccountNumber, amount,description);
    }

    @ExceptionHandler({AccountNotFoundException.class, InsufficientFundsException.class,AccountNotFoundException.class,AccountNotActiveException.class})
    public ResponseEntity<String> handleAccountExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}