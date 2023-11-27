package zw.co.tech263.transaction_processing_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.tech263.transaction_processing_service.dto.ErrorResponse;
import zw.co.tech263.transaction_processing_service.exception.*;
import zw.co.tech263.transaction_processing_service.model.Transaction;
import zw.co.tech263.transaction_processing_service.service.AccountService;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("api/v1/accounts")
public class AccountController {
    private static final Logger logger = LogManager.getLogger(AccountController.class);

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/{accountNumber}/deposit")
    @Operation(summary = "Deposit funds into an account")
    @ApiResponse(responseCode = "200", description = "Deposit completed successfully")
    @ApiResponse(responseCode = "400", description = "Deposit failed", content = @Content(schema = @Schema(implementation = String.class)))
    public void deposit(
            @PathVariable("accountNumber") String accountNumber,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("purpose") String description
    ) throws AccountNotFoundException, AccountNotActiveException, URISyntaxException, InvalidAmountExeption {
        logger.info("Deposit request received for accountNumber: {}", accountNumber);
        logger.debug("Deposit details - AccountNumber: {}, Amount: {}, Purpose: {}", accountNumber, amount, description);

        try {
            accountService.deposit(accountNumber, amount, description);
            logger.info("Deposit completed successfully for accountNumber: {}", accountNumber);
        } catch (AccountNotFoundException | AccountNotActiveException | URISyntaxException | InvalidAmountExeption e) {
            logger.error("Deposit failed for accountNumber: {}. Reason: {}", accountNumber, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/{accountNumber}/withdraw")
    @Operation(summary = "Withdraw funds from an account")
    @ApiResponse(responseCode = "200", description = "Withdrawal completed successfully")
    @ApiResponse(responseCode = "400", description = "Withdrawal failed", content = @Content(schema = @Schema(implementation = String.class)))
    public void withdraw(
            @PathVariable("accountNumber") String accountNumber,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("purpose") String description
    ) throws InsufficientFundsException, AccountNotFoundException, AccountNotActiveException, URISyntaxException, InvalidAmountExeption {
        logger.info("Withdrawal request received for accountNumber: {}", accountNumber);
        logger.debug("Withdrawal details - AccountNumber: {}, Amount: {}, Purpose: {}", accountNumber, amount, description);

        try {
            accountService.withdraw(accountNumber, amount, description);
            logger.info("Withdrawal completed successfully for accountNumber: {}", accountNumber);
        } catch (InsufficientFundsException | AccountNotFoundException | AccountNotActiveException |
                 URISyntaxException | InvalidAmountExeption e) {
            logger.error("Withdrawal failed for accountNumber: {}. Reason: {}", accountNumber, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{accountNumber}/balance")
    @Operation(summary = "Get the account balance")
    @ApiResponse(responseCode = "200", description = "Balance retrieved successfully", content = @Content(schema = @Schema(implementation = BigDecimal.class)))
    @ApiResponse(responseCode = "400", description = "Failed to retrieve balance", content = @Content(schema = @Schema(implementation = String.class)))
    public BigDecimal getBalance(@PathVariable("accountNumber") String accountNumber) throws AccountNotFoundException {
        logger.info("Balance request received for accountNumber: {}", accountNumber);

        try {
            BigDecimal balance = accountService.getBalance(accountNumber);
            logger.info("Balance retrieved successfully for accountNumber: {}. Balance: {}", accountNumber, balance);
            return balance;
        } catch (AccountNotFoundException e) {
            logger.error("Failed to retrieve balance for accountNumber: {}. Reason: {}", accountNumber, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{accountNumber}/transactions")
    @Operation(summary = "Get the last transactions of an account")
    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully", content = @Content(schema = @Schema(implementation = List.class)))
    @ApiResponse(responseCode = "400", description = "Failed to retrieve transactions", content = @Content(schema = @Schema(implementation = String.class)))
    public List<Transaction> getTransactions(
            @PathVariable("accountNumber") String accountNumber,
            @RequestParam("number_of_transactions") int transactionCount
    ) throws AccountNotFoundException,TransactionsNotFoundException {
        logger.info("Transactions request received for accountNumber: {}", accountNumber);
        logger.debug("Transactions details - AccountNumber: {}, TransactionCount: {}", accountNumber, transactionCount);

        try {
            List<Transaction> transactions = accountService.getLastTransactions(accountNumber, transactionCount);
            logger.info("Transactions retrieved successfully for accountNumber: {}", accountNumber);
            return transactions;
        } catch (TransactionsNotFoundException e) {
            logger.error("Failed to retrieve transactions for accountNumber: {}. Reason: {}", accountNumber, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/{accountNumber}/transfer")
    @Operation(summary = "Transfer funds from one account to another")
    @ApiResponse(responseCode = "200", description = "Transfer completed successfully")
    @ApiResponse(responseCode = "400", description = "Transfer failed", content = @Content(schema = @Schema(implementation = String.class)))
    public void transferFunds(
            @PathVariable("accountNumber") String accountNumber,
            @RequestParam("destinationAccountNumber") String destinationAccountNumber,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("purpose") String description
    ) throws InsufficientFundsException, AccountNotFoundException, InvalidAmountExeption {
        logger.info("Transfer request received from accountNumber: {} to destinationAccountNumber: {}", accountNumber, destinationAccountNumber);
        logger.debug("Transfer details - AccountNumber: {}, DestinationAccountNumber: {}, Amount: {}, Purpose: {}", accountNumber,destinationAccountNumber, amount, description);

        try {
            accountService.transferFunds(accountNumber, destinationAccountNumber, amount, description);
            logger.info("Transfer completed successfully from accountNumber: {} to destinationAccountNumber: {}", accountNumber, destinationAccountNumber);
        } catch (InsufficientFundsException | AccountNotFoundException | InvalidAmountExeption e) {
            logger.error("Transfer failed from accountNumber: {} to destinationAccountNumber: {}. Reason: {}", accountNumber, destinationAccountNumber, e.getMessage());
            throw  e;
        }
    }

    @ExceptionHandler({AccountNotFoundException.class, InsufficientFundsException.class, AccountNotActiveException.class,InvalidAmountExeption.class, TransactionsNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleAccountExceptions(Exception ex) {
        logger.error("An exception occurred: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorDescription(ex.getMessage())
                .errorCode("400")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}