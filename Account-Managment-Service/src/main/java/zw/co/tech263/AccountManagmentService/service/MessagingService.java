package zw.co.tech263.AccountManagmentService.service;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zw.co.tech263.AccountManagmentService.RabbitMQConfig;
import zw.co.tech263.AccountManagmentService.dto.messaging.CustomerSupportAccountMessage;
import zw.co.tech263.AccountManagmentService.dto.messaging.NotificationMessage;
import zw.co.tech263.AccountManagmentService.dto.messaging.TransactionServiceMessage;


@Service
public class MessagingService {



    private final RabbitTemplate template;

    private final RabbitMQConfig rabbitMQConfig;


    @Autowired
    public MessagingService(RabbitTemplate template,RabbitMQConfig rabbitMQConfig){
        this.template=template;
        this.rabbitMQConfig=rabbitMQConfig;

    }


    public void sendNotification(NotificationMessage notificationMessage){
        template.convertAndSend(rabbitMQConfig.getEXCHANGE_NAME(),
                rabbitMQConfig.getNOTIFICATION_ROUTING_KEY(), notificationMessage);
    }

    public void createCustomerSupportAccount(CustomerSupportAccountMessage message){
        template.convertAndSend(rabbitMQConfig.getEXCHANGE_NAME(),
                rabbitMQConfig.getCUSTOMER_SERVICES_ROUTING_KEY(), message);
    }

    public void createTransactionServiceAccount(TransactionServiceMessage message){
        template.convertAndSend(rabbitMQConfig.getEXCHANGE_NAME(),
                rabbitMQConfig.getTRANSACTION_ROUTING_KEY(), message);
    }
}
