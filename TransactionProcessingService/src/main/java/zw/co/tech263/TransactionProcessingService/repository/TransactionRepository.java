package zw.co.tech263.TransactionProcessingService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import zw.co.tech263.TransactionProcessingService.model.Transaction;

import java.util.List;
import java.util.Optional;


public interface TransactionRepository extends JpaRepository<Transaction,Long> {


    @Query("SELECT t FROM Transaction t WHERE t.accountNumber = ?1 ORDER BY t.createdAt DESC")
    Optional<List<Transaction>> findLast10ByAccountNumber(String accountNumber, Pageable pageable);

}
