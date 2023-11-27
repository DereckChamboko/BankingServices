package zw.co.tech263.transaction_processing_service.service;

import zw.co.tech263.transaction_processing_service.dto.CustomerAccount;
import zw.co.tech263.transaction_processing_service.exception.*;
import zw.co.tech263.transaction_processing_service.model.Account;
import zw.co.tech263.transaction_processing_service.model.Transaction;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;

public interface AccountService {




    public void deposit(String accountNumber, BigDecimal amount, String description) throws AccountNotFoundException, AccountNotActiveException, URISyntaxException, InvalidAmountExeption;

    public void withdraw(String accountNumber, BigDecimal amount,String description) throws AccountNotFoundException, InsufficientFundsException, AccountNotActiveException, URISyntaxException, InvalidAmountExeption;

    public BigDecimal getBalance(String accountNumber) throws AccountNotFoundException;


    public List<Transaction> getLastTransactions(String accountNumber, int lastTransactionCount) throws TransactionsNotFoundException;

    public void transferFunds(String accountNumber, String destinationAccountNumber, BigDecimal amount, String description) throws InsufficientFundsException, AccountNotFoundException, InvalidAmountExeption;

    public Account addNewCustomerAccount(String accountNumber);

    public CustomerAccount getAccountDetails(String accountNumber) throws AccountNotFoundException, URISyntaxException;



}
