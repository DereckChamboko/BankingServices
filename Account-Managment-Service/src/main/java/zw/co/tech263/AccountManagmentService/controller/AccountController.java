package zw.co.tech263.AccountManagmentService.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.tech263.AccountManagmentService.dto.Customer;
import zw.co.tech263.AccountManagmentService.dto.StatusUpdate;
import zw.co.tech263.AccountManagmentService.exception.AccountNotFoundException;
import zw.co.tech263.AccountManagmentService.exception.InvalidAccountTypeException;
import zw.co.tech263.AccountManagmentService.exception.InvalidStatusException;
import zw.co.tech263.AccountManagmentService.service.AccountManagementServiceImp;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {


    @Autowired
    AccountManagementServiceImp accountManagementService;


    @PostMapping
    @Operation(summary = "Create account", description = "New account is created and set as active. Complete account details are returned")
    public ResponseEntity addNewAccount(@RequestBody Customer customer){
        try{
            return ResponseEntity.ok(accountManagementService.addAccount(customer));
        }catch (InvalidAccountTypeException iate){
            return ResponseEntity.badRequest().body(iate.getMessage());

        }


    }

    @GetMapping
    public ResponseEntity getAllAccounts(){
        return accountManagementService.getAllAccount();
    }
    @GetMapping("{accountNumber}")
    public ResponseEntity getAccountByAccountNumber(@PathVariable String accountNumber){
        try {

             return ResponseEntity.ok(
                     accountManagementService.getAccountByAccountNumber(accountNumber)
             );
        }catch (AccountNotFoundException anf){
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping
    public ResponseEntity updateAccount(@RequestBody Customer customer){

        try {

            return ResponseEntity.ok(
                    accountManagementService.updateAccount(customer)
            );
        }catch (AccountNotFoundException anf){
            return ResponseEntity.notFound().build();
        } catch (InvalidAccountTypeException iate) {
            return ResponseEntity.badRequest().body(iate.getMessage());
        }

    }





    @PutMapping("/{accountNumber}/status")


    @Operation(summary = "Update Account Status", description = "Updates the status of an account identified by its account number")
    @ApiResponse(responseCode = "200", description = "Successful operation. Account status updated")
    @ApiResponse(responseCode = "400", description = "Invalid status provided. The request is malformed", content = @Content(schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "404", description = "Account not found. The specified account number does not exist")
    public ResponseEntity updateAccountStatus(@PathVariable String  accountNumber,@RequestBody StatusUpdate statusUpdate){

        try {
            return ResponseEntity.ok(
                    accountManagementService.updateAccountStatus(statusUpdate,accountNumber)
            );
        }catch (AccountNotFoundException anf){
            return ResponseEntity.notFound().build();
        } catch (InvalidStatusException ise) {
            return ResponseEntity.badRequest().body(ise.getMessage());
        }
    }


/*


    public ResponseEntity getAccountBalance(){

    }


    public void setAccountBalance(){

    }

 */


}