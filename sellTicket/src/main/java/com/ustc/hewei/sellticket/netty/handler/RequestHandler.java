package com.ustc.hewei.sellticket.netty.handler;

import com.ustc.hewei.sellticket.entity.TicketOrder;
import com.ustc.hewei.sellticket.service.ITicketOrderService;
import com.ustc.hewei.sellticket.service.ITicketStockService;
import com.ustc.hewei.sellticket.utils.Constants;
import com.ustc.hewei.sellticket.utils.RedisId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import warp.Request;
import warp.Response;

import javax.annotation.Resource;
import java.util.Collections;

import static com.ustc.hewei.sellticket.utils.Constants.USERID_KEY;

/**
 * @author hewei
 * @version 1.0
 * @description: 处理客户端请求
 * @date 2022/10/21 14:17
 */

@Component
public class RequestHandler {
    private final static Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisId redisId;
    @Resource
    private ITicketStockService ticketStockService;
    @Resource
    private ITicketOrderService ticketOrderService;
    private static final DefaultRedisScript<Long> TICKET_SCRIPT;

    static {
        TICKET_SCRIPT = new DefaultRedisScript<>();
        TICKET_SCRIPT.setResultType(Long.class);
        TICKET_SCRIPT.setLocation(new ClassPathResource("sellTicket.lua"));
    }

    /*
     * @description: 抢票功能的实现
     * @author: hewei
     * @date: 2022/10/24 21:56
     **/
    public Response handlerRequest(Request request) {
        String token = request.getToken();
        String userId = stringRedisTemplate.opsForValue().get(USERID_KEY + token);
        if (userId == null) {
            return Response.fail(request, "未知的token" + "{}" + token + "!");
        }
        Long ticketId = request.getTicketId();
        Long execute = stringRedisTemplate.execute(TICKET_SCRIPT, Collections.emptyList(), ticketId.toString(), userId);
        int result = execute.intValue();
        if (result == 1) {
            return Response.fail(request, "库存不足!");
        }
        if (result == 2) {
            return Response.fail(request, "重复下单!");
        }
        long orderId = redisId.nextId("order");
        TicketOrder ticketOrder = new TicketOrder();
        ticketOrder.setTicketId(ticketId);
        ticketOrder.setId(orderId);
        ticketOrder.setUserId(Long.valueOf(userId));
        try {
            rabbitTemplate.convertAndSend("ticketOrderExchange", "order", ticketOrder);
        } catch (AmqpException e) {
            logger.error("mq异常 {}, 订单信息 {}", e.getMessage(), ticketOrder);
        }
        return Response.ok(request, orderId);
    }

    @Transactional
    public void handlerOrder(TicketOrder order) {
        Long ticketId = order.getTicketId();
        ticketStockService.update().eq("id", ticketId).setSql("stock = stock - 1").update();
        ticketOrderService.save(order);
    }
}
