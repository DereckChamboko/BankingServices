package zw.co.tech263.AccountManagmentService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import zw.co.tech263.AccountManagmentService.dto.request.CustomerCreationDto;
import zw.co.tech263.AccountManagmentService.dto.request.CustomerUpdateDto;
import zw.co.tech263.AccountManagmentService.dto.StatusUpdate;
import zw.co.tech263.AccountManagmentService.exception.AccountNotFoundException;
import zw.co.tech263.AccountManagmentService.exception.InvalidAccountTypeException;
import zw.co.tech263.AccountManagmentService.exception.InvalidStatusException;
import zw.co.tech263.AccountManagmentService.service.AccountManagementServiceImp;

import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@Transactional
public class AccountController {

    @Autowired
    AccountManagementServiceImp accountManagementService;

    @PostMapping
    @Operation(summary = "Create Account", description = "Creates a new account and sets it as active. Returns the complete account details.")
    @ApiResponse(responseCode = "200", description = "Successful operation. New account created.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerUpdateDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid account type provided. The request is malformed.", content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "404", description = "Account not found. The specified account number does not exist.", content = @Content(mediaType = "text/plain"))
    public ResponseEntity addNewAccount(@RequestBody CustomerCreationDto customerCreationDto) throws InvalidAccountTypeException, URISyntaxException, AccountNotFoundException {
        return ResponseEntity.ok(accountManagementService.addAccount(customerCreationDto));
    }

    @GetMapping
    @Operation(summary = "Get All Accounts", description = "Retrieves all existing accounts.")
    @ApiResponse(responseCode = "200", description = "Successful operation. Returns the list of accounts.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class, type = "List<Customer>")))
    public ResponseEntity getAllAccounts() {
        return accountManagementService.getAllAccount();
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get Account by Account Number", description = "Retrieves an account based on its account number.")
    @ApiResponse(responseCode = "200", description = "Successful operation. Returns the account details.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerUpdateDto.class)))
    @ApiResponse(responseCode = "404", description = "Account not found. The specified account number does not exist.", content = @Content(mediaType = "text/plain"))
    public ResponseEntity getAccountByAccountNumber(@PathVariable String accountNumber) throws AccountNotFoundException {
        return ResponseEntity.ok(accountManagementService.getAccountByAccountNumber(accountNumber));
    }

    @PutMapping
    @Operation(summary = "Update Account", description = "Updates an existing account.")
    @ApiResponse(responseCode = "200", description = "Successful operation. Account updated.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerUpdateDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid account type provided. The request is malformed.", content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "404", description = "Account not found. The specified account number does not exist.", content = @Content(mediaType = "text/plain"))
    public ResponseEntity updateAccount(@RequestBody CustomerUpdateDto customerUpdateDto) throws InvalidAccountTypeException, AccountNotFoundException {
        return ResponseEntity.ok(accountManagementService.updateAccount(customerUpdateDto));
    }

    @PutMapping("/{accountNumber}/status")
    @Operation(summary = "Update Account Status", description = "Updates the status of an account identified by its account number.")
    @ApiResponse(responseCode = "200", description = "Successful operation. Account status updated.")
    @ApiResponse(responseCode = "400", description = "Invalid status provided. The request is malformed.", content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "404", description = "Account not found. The specified account number does not exist.", content = @Content(mediaType = "text/plain"))
    public ResponseEntity updateAccountStatus(@PathVariable String accountNumber, @RequestBody StatusUpdate statusUpdate) throws InvalidStatusException, AccountNotFoundException {
        return ResponseEntity.ok(accountManagementService.updateAccountStatus(statusUpdate, accountNumber));
    }

    @ExceptionHandler({AccountNotFoundException.class, InvalidAccountTypeException.class, InvalidStatusException.class})
    public ResponseEntity<String> handleAccountExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}