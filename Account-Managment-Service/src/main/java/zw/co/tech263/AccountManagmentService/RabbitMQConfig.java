package zw.co.tech263.AccountManagmentService;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${notification.queue}")
    private String NOTIFICATION_QUEUE;

    @Value("${transaction.processing.queue}")
    private String TRANSACTION_PROCESSING_QUEUE;

    @Value("${customer.services.queue}")
    private String CUSTOMER_SERVICES_QUEUE;

    public String getNOTIFICATION_QUEUE() {
        return NOTIFICATION_QUEUE;
    }

    public String getTRANSACTION_PROCESSING_QUEUE() {
        return TRANSACTION_PROCESSING_QUEUE;
    }

    public String getCUSTOMER_SERVICES_QUEUE() {
        return CUSTOMER_SERVICES_QUEUE;
    }

    public String getEXCHANGE_NAME() {
        return EXCHANGE_NAME;
    }

    public String getNOTIFICATION_ROUTING_KEY() {
        return NOTIFICATION_ROUTING_KEY;
    }

    public String getTRANSACTION_ROUTING_KEY() {
        return TRANSACTION_ROUTING_KEY;
    }

    public String getCUSTOMER_SERVICES_ROUTING_KEY() {
        return CUSTOMER_SERVICES_ROUTING_KEY;
    }

    @Value("${exchange.name}")
    private String EXCHANGE_NAME;

    @Value("${notification.routing.key}")
    private String NOTIFICATION_ROUTING_KEY;

    @Value("${transaction.routing.key}")
    private String TRANSACTION_ROUTING_KEY;

    @Value("${customer.services.routing.key}")
    private String CUSTOMER_SERVICES_ROUTING_KEY;

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE);
    }

    @Bean
    public Queue transactionServiceQueue() {
        return new Queue(TRANSACTION_PROCESSING_QUEUE);
    }

    @Bean
    public Queue customerServiceQueue() {
        return new Queue(CUSTOMER_SERVICES_QUEUE);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(exchange)
                .with(NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public Binding transactionProcessingBinding(Queue transactionServiceQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(transactionServiceQueue)
                .to(exchange)
                .with(TRANSACTION_ROUTING_KEY);
    }

    @Bean
    public Binding customerServicesBinding(Queue customerServiceQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(customerServiceQueue)
                .to(exchange)
                .with(CUSTOMER_SERVICES_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}