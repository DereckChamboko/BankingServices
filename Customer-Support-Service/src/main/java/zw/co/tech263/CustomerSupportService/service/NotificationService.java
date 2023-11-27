package zw.co.tech263.CustomerSupportService.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zw.co.tech263.CustomerSupportService.dto.message.RabbitMQMessageOut;
import zw.co.tech263.CustomerSupportService.RabbitMQConfig;

@Service
public class NotificationService {
    private static final Logger logger = LogManager.getLogger(NotificationService.class);

    private final RabbitTemplate template;
    private final RabbitMQConfig rabbitMQConfig;

    @Autowired
    public NotificationService(RabbitTemplate template, RabbitMQConfig rabbitMQConfig) {
        this.template = template;
        this.rabbitMQConfig = rabbitMQConfig;
    }

    public void sendNotification(RabbitMQMessageOut rabbitMQMessageOut) {
        logger.info("Sending notification: {}", rabbitMQMessageOut);
        try {
            template.convertAndSend(rabbitMQConfig.getExchangeName(), rabbitMQConfig.getRoutingKey(), rabbitMQMessageOut);
            logger.info("Notification sent successfully: {}", rabbitMQMessageOut);
        } catch (Exception e) {
            logger.error("Failed to send notification: {}", e.getMessage());
            throw e;
        }
    }
}