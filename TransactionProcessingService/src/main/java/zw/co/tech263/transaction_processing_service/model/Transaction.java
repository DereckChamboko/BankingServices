package zw.co.tech263.transaction_processing_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "Transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Column(name = "accountNumber")
    private String accountNumber;

    @Column(name = "dr")
    private BigDecimal dr;

    @Column(name = "cr")
    private BigDecimal cr;

    private String description;

    @Column(name = "created_at")
    private long createdAt;


    @ManyToOne
    @JoinColumn(name = "accountNumber", referencedColumnName = "accountNumber", insertable = false, updatable = false)
    private Account account;



}