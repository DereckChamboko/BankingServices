package zw.co.tech263.TransactionProcessingService.dto.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zw.co.tech263.TransactionProcessingService.service.AccountServiceImp;

@Component
public class RabbitMQMessageConsumer {

    @Autowired
    AccountServiceImp accountService;

    @RabbitListener(queues = "Equals_Transaction_processing")
    public void consumeMessage(String message) throws JsonProcessingException {

        ObjectMapper objectMapper=new ObjectMapper();
        var accountDTO=objectMapper.readValue(message,AccountDTO.class);
        System.out.println("****************Creating account "+accountDTO.getAccountNumber());
        accountService.addNewCustomerAccount(accountDTO.getAccountNumber());



    }
}