package zw.co.tech263.AccountManagmentService.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.tech263.AccountManagmentService.dto.Customer;
import zw.co.tech263.AccountManagmentService.exception.AccountNotFoundException;
import zw.co.tech263.AccountManagmentService.exception.InvalidAccountTypeException;
import zw.co.tech263.AccountManagmentService.service.AccountManagementServiceImp;

@RestController
public class AccountManagementController {


    @Autowired
    AccountManagementServiceImp accountManagementService;


    @PostMapping("/account")
    public ResponseEntity addNewAccount(@RequestBody Customer customer){
        try{
            return ResponseEntity.ok(accountManagementService.addAccount(customer));
        }catch (InvalidAccountTypeException iate){
            return ResponseEntity.badRequest().body(iate.getMessage());

        }


    }

    @GetMapping("/account")
    public ResponseEntity getAllAccounts(){
        return accountManagementService.getAllAccount();
    }
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity getAccountByAccountNumber(@PathVariable String accountNumber){
        try {

             return ResponseEntity.ok(
                     accountManagementService.getAccountByAccountNumber(accountNumber)
             );
        }catch (AccountNotFoundException anf){
            return ResponseEntity.notFound().build();
        }
    }
/*
    public ResponseEntity updateAccount(){

    }

    public ResponseEntity getAccountDetails(String id){

    }

    public ResponseEntity getAccountBalance(){

    }

    public ResponseEntity getLast10Ttrasactions(){

    }

 */


}
