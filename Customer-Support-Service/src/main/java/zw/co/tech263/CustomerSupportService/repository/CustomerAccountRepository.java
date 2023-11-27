package zw.co.tech263.CustomerSupportService.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import zw.co.tech263.CustomerSupportService.model.CustomerAccount;

import java.util.Optional;

@Repository
public interface CustomerAccountRepository extends MongoRepository<CustomerAccount, String> {



    @Query("{'accountNumber':?0}")
    Optional<CustomerAccount> findByAccountNumber(String accountNumber);
}
