package com.ustc.hewei.sellticket.config;

import com.ustc.hewei.sellticket.consumer.OrderHandler;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author hewei
 * @version 1.0
 * @description: 设置手动确认
 * @date 2022/10/24 10:05
 */

@Configuration
public class MessageListenerConfig {

    @Resource
    private OrderHandler orderHandler;
    @Resource
    private CachingConnectionFactory connectionFactory;

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setQueueNames("ticketOrder");
        container.setMessageListener(orderHandler);
        return container;
    }
}
