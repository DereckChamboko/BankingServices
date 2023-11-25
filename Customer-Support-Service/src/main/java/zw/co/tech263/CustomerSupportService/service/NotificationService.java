package zw.co.tech263.CustomerSupportService.service;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zw.co.tech263.CustomerSupportService.config.RabbitMQConfig;
import zw.co.tech263.CustomerSupportService.dto.message.RabbitMQMessageOut;


@Service
public class NotificationService {


    @Autowired
    RabbitTemplate template;


    public void sendNotification(RabbitMQMessageOut rabbitMQMessageOut){
        template.convertAndSend(RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.QUEUE_ROUTING_KEY, rabbitMQMessageOut);
    }
}
