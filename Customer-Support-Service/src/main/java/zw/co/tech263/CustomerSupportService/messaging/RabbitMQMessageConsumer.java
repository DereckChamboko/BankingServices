package zw.co.tech263.CustomerSupportService.messaging;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zw.co.tech263.CustomerSupportService.dto.request.CustomerAccountDTO;
import zw.co.tech263.CustomerSupportService.exception.DuplicateAccountException;
import zw.co.tech263.CustomerSupportService.service.CustomerAccountService;

@Component
public class RabbitMQMessageConsumer {



    private static final Logger logger = LogManager.getLogger(RabbitMQMessageConsumer.class);

    private final CustomerAccountService customerAccountService;

    @Autowired
    public RabbitMQMessageConsumer(CustomerAccountService customerAccountService) {
        this.customerAccountService = customerAccountService;
    }

    @RabbitListener(queues = "Equals_Account_customer_services")
    public void consumeMessage(CustomerAccountDTO accountDTO) throws DuplicateAccountException {
        logger.info("Received message from RabbitMQ for creating customer account. AccountNumber: {}", accountDTO.getAccountNumber());
        try {
            customerAccountService.createCustomerAccount(accountDTO);
            logger.info("Customer account created successfully. AccountNumber: {}", accountDTO.getAccountNumber());
        } catch (DuplicateAccountException e) {
            logger.error("Failed to create customer account. AccountNumber: {}. Reason: {}", accountDTO.getAccountNumber(), e.getMessage());
            throw e;
        }
    }
}