package zw.co.tech263.TransactionProcessingService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import zw.co.tech263.TransactionProcessingService.model.Account;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {


        @Query("SELECT ac FROM Account ac WHERE ac.accountNumber = ?1")
        Optional<Account> findByAccountNumber(String accountNumber);






}
