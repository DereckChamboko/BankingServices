package zw.co.tech263.AccountManagmentService.service;

        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.ResponseEntity;
        import org.springframework.stereotype.Service;
        import org.springframework.web.client.RestTemplate;
        import zw.co.tech263.AccountManagmentService.dto.messaging.TransactionServiceMessage;
        import zw.co.tech263.AccountManagmentService.dto.request.CustomerCreationDto;
        import zw.co.tech263.AccountManagmentService.dto.request.CustomerUpdateDto;
        import zw.co.tech263.AccountManagmentService.dto.StatusUpdate;
        import zw.co.tech263.AccountManagmentService.dto.messaging.CustomerSupportAccountMessage;
        import zw.co.tech263.AccountManagmentService.dto.messaging.NotificationMessage;
        import zw.co.tech263.AccountManagmentService.exception.AccountNotFoundException;
        import zw.co.tech263.AccountManagmentService.exception.InvalidAccountTypeException;
        import zw.co.tech263.AccountManagmentService.exception.InvalidStatusException;
        import zw.co.tech263.AccountManagmentService.model.AccountStatus;
        import zw.co.tech263.AccountManagmentService.model.AccountType;
        import zw.co.tech263.AccountManagmentService.model.CustomerAccount;
        import zw.co.tech263.AccountManagmentService.repository.AccountRepository;

        import java.net.URISyntaxException;
        import java.util.Arrays;
        import java.util.List;
        import java.util.Objects;

@Service
public class AccountManagementServiceImp implements AccountManagementService {

    private static final Logger logger = LoggerFactory.getLogger(AccountManagementServiceImp.class);

    private final AccountRepository accountRepository;
    private final RestTemplate restTemplate;
    private final MessagingService messagingService;

    @Autowired
    public AccountManagementServiceImp(AccountRepository accountRepository, RestTemplate restTemplate, MessagingService messagingService) {
        this.accountRepository = accountRepository;
        this.restTemplate = restTemplate;
        this.messagingService = messagingService;
    }


    public CustomerAccount addAccount(CustomerCreationDto account) throws InvalidAccountTypeException, AccountNotFoundException, URISyntaxException {
        logger.info("Adding account: {}", account);

        CustomerAccount customerAccount = CustomerAccount.builder()
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .address(account.getAddress())
                .accountStatus(AccountStatus.ACTIVE)
                .accountType(getValidAccountType(account.getAccountType()))
                .build();
        customerAccount = accountRepository.save(customerAccount);

        NotificationMessage message = NotificationMessage.builder()
                .accountNumber(customerAccount.getAccountNumber())
                .messageTittle("New account created - " + customerAccount.getAccountNumber())
                .message("We are excited to have you onboard")
                .build();
        messagingService.sendNotification(message);
        messagingService.createCustomerSupportAccount(new CustomerSupportAccountMessage(customerAccount.getAccountNumber()));
        messagingService.createTransactionServiceAccount(new TransactionServiceMessage(customerAccount.getAccountNumber()));

        logger.info("Account added successfully: {}", customerAccount);
        return customerAccount;
    }

    public ResponseEntity<List<CustomerAccount>> getAllAccount() {
        logger.info("Fetching all accounts");
        List<CustomerAccount> allAccounts = accountRepository.findAll();
        logger.info("Fetched all accounts: {}", allAccounts);
        return ResponseEntity.ok(allAccounts);
    }

    public CustomerAccount getAccountByAccountNumber(String accountNumber) throws AccountNotFoundException {
        logger.info("Fetching account by account number: {}", accountNumber);
        CustomerAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Invalid account number: " + accountNumber));
        logger.info("Fetched account: {}", account);
        return account;
    }

    public CustomerAccount updateAccount(CustomerUpdateDto account) throws InvalidAccountTypeException, AccountNotFoundException {
        logger.info("Updating account: {}", account);

        CustomerAccount customerAccount = getAccountByAccountNumber(account.getAccountNumber());
        if(account.getAccountType()!=null) {
            customerAccount.setAccountType(getValidAccountType(account.getAccountType()));
        }

        customerAccount.setFirstName(Objects.requireNonNullElse(account.getFirstName(), customerAccount.getFirstName()));
        customerAccount.setLastName(Objects.requireNonNullElse(account.getLastName(), customerAccount.getLastName()));
        customerAccount.setAddress(Objects.requireNonNullElse(account.getAddress(), customerAccount.getAddress()));

        customerAccount = accountRepository.save(customerAccount);

        logger.info("Account updated successfully: {}", customerAccount);
        return customerAccount;
    }

    public CustomerAccount updateAccountStatus(StatusUpdate statusUpdate, String accountNumber) throws InvalidStatusException, AccountNotFoundException {
        logger.info("Updating account status: {}, Account Number: {}", statusUpdate, accountNumber);

        CustomerAccount customerAccount = getAccountByAccountNumber(accountNumber);
        boolean isValidStatus = Arrays.stream(AccountStatus.values())
                .anyMatch(accountStatus -> accountStatus.name().equals(statusUpdate.getStatus()));

        if (isValidStatus) {
            customerAccount.setAccountStatus(AccountStatus.valueOf(statusUpdate.getStatus()));
        } else {
            throw new InvalidStatusException("Invalid status: " + statusUpdate.getStatus() + ". Was expecting " + Arrays.toString(AccountStatus.values()));
        }

        customerAccount = accountRepository.save(customerAccount);

        logger.info("Account status updated successfully: {}", customerAccount);
        return customerAccount;
    }

    private AccountType getValidAccountType(String accountTypeToValidate) throws InvalidAccountTypeException {
        logger.info("Validating account type: {}", accountTypeToValidate);

        if (accountTypeToValidate != null) {
            boolean isValidAccountType = Arrays.stream(AccountType.values())
                    .anyMatch(accountType -> accountType.name().equals(accountTypeToValidate));

            if (isValidAccountType) {
                return AccountType.valueOf(accountTypeToValidate);
            }

            throw new InvalidAccountTypeException("Invalid account type: " + accountTypeToValidate +
                    ". Expecting any of the following: " + Arrays.toString(AccountType.values()));
        }

        throw new InvalidAccountTypeException("Missing account type. Expecting any of the following: " +
                Arrays.toString(AccountType.values()));
    }
}