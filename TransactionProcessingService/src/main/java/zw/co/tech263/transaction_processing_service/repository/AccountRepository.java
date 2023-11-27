package zw.co.tech263.transaction_processing_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import zw.co.tech263.transaction_processing_service.model.Account;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {


        @Query("SELECT ac FROM Account ac WHERE ac.accountNumber = ?1")
        Optional<Account> findByAccountNumber(String accountNumber);






}
