package zw.co.tech263.AccountManagmentService.controller;


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
public class AccountManagementController {


    @Autowired
    AccountManagementServiceImp accountManagementService;


    @PostMapping
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
