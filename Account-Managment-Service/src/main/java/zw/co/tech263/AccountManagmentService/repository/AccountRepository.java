package zw.co.tech263.AccountManagmentService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import zw.co.tech263.AccountManagmentService.model.CustomerAccount;

import java.util.Optional;


@Repository
public interface AccountRepository extends JpaRepository<CustomerAccount, Long> {


    @Query("SELECT ca FROM CustomerAccount ca WHERE ca.accountNumber = ?1")
    Optional<CustomerAccount> findByAccountNumber(String accountNumber);

}
