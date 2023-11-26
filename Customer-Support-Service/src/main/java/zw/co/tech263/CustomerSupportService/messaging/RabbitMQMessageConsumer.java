package zw.co.tech263.CustomerSupportService.messaging;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zw.co.tech263.CustomerSupportService.dto.request.CustomerAccountDTO;
import zw.co.tech263.CustomerSupportService.exception.DuplicateAccountException;
import zw.co.tech263.CustomerSupportService.service.CustomerAccountService;

@Component
public class RabbitMQMessageConsumer {

    @Autowired
    CustomerAccountService customerAccountService;

    @RabbitListener(queues = "Equals_Account_customer_services")
    public void consumeMessage(CustomerAccountDTO accountDTO) throws DuplicateAccountException {
        System.out.println("****************** Creating customer "+accountDTO.getAccountNumber());
        customerAccountService.createCustomerAccount(accountDTO);

    }
}