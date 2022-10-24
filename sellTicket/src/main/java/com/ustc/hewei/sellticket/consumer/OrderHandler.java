package com.ustc.hewei.sellticket.consumer;

import com.rabbitmq.client.Channel;
import com.ustc.hewei.sellticket.entity.TicketOrder;
import com.ustc.hewei.sellticket.netty.handler.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * @author hewei
 * @version 1.0
 * @description: mq消费者
 * @date 2022/10/21 15:24
 */

@Component
public class OrderHandler implements ChannelAwareMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(OrderHandler.class);
    @Resource
    private RequestHandler requestHandler;

//    @RabbitListener(queues = "ticketOrder")
//    public void listenOrder(TicketOrder order) {
//        requestHandler.handlerOrder(order);
//        logger.info("订单 {}, 处理完成!", order.getId());
//    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        TicketOrder order = null;
        try {
            byte[] data = message.getBody();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            order = (TicketOrder) ois.readObject();
            ois.close();
            requestHandler.handlerOrder(order);
            channel.basicAck(deliveryTag, true);
            logger.info("订单 {}, 处理完成", order.getId());
        } catch (Exception e) {
            // 不放回，人为对错误日志进行处理
            channel.basicReject(deliveryTag, false);
            logger.error("订单处理失败!, 订单详情 {}", order);
            e.printStackTrace();
        }
    }
}
