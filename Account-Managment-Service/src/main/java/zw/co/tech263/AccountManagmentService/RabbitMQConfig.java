package zw.co.tech263.AccountManagmentService;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String NOTIFICATION_QUEUE = "Equals_Account_notification";
    public static final String TRANSACTION_PROCESSING_QUEUE = "Equals_Account_processing";
    public static final String CUSTOMER_SERVICES_QUEUE = "Equals_Account_customer_services";
    public static final String EXCHANGE_NAME = "Equals.exchange";
    public static final String NOTIFICATION_ROUTING_KEY = "Equals.customer.notification";
    public static final String TRANSACTION_ROUTING_KEY = "Equals.customer.transactions";
    public static final String CUSTOMER_SERVICES_ROUTING_KEY = "Equals.customer.services";

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