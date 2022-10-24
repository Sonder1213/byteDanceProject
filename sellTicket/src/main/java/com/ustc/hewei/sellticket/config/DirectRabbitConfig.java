package com.ustc.hewei.sellticket.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hewei
 * @version 1.0
 * @description: 直连交换机配置类
 * @date 2022/10/24 10:32
 */

@Configuration
public class DirectRabbitConfig {

    @Bean
    public Queue myQueue() {
        return new Queue("ticketOrder",true);
    }

    @Bean
    public DirectExchange myExchange() {
        return new DirectExchange("ticketOrderExchange",true,false);
    }

    @Bean
    public Binding bindingDirect() {
        return BindingBuilder.bind(myQueue()).to(myExchange()).with("order");
    }
}
