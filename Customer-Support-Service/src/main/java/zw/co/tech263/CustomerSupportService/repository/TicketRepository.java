package zw.co.tech263.CustomerSupportService.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import zw.co.tech263.CustomerSupportService.model.Ticket;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {
    // Add any custom query methods if needed
}