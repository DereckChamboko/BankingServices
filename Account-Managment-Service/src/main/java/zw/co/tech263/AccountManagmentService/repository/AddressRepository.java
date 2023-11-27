package zw.co.tech263.AccountManagmentService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zw.co.tech263.AccountManagmentService.model.Address;


@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
