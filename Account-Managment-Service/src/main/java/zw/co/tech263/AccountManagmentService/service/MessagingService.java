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


    @Autowired
    public MessagingService(RabbitTemplate template){
        this.template=template;

    }


    public void sendNotification(NotificationMessage notificationMessage){
        template.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY, notificationMessage);
    }

    public void createCustomerSupportAccount(CustomerSupportAccountMessage message){
        template.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.CUSTOMER_SERVICES_ROUTING_KEY, message);
    }

    public void createTransactionServiceAccount(TransactionServiceMessage message){
        template.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.TRANSACTION_ROUTING_KEY, message);
    }
}
