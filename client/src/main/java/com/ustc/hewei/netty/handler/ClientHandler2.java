package com.ustc.hewei.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import warp.Request;
import warp.Response;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hewei
 * @version 1.0
 * @description: 长连接handler
 * @date 2022/10/22 15:30
 */

public class ClientHandler2 extends SimpleChannelInboundHandler<Response> {

    private final ConcurrentHashMap<String, Long> waitResponse;
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler2.class);
    private Channel channel;

    public ClientHandler2() {
        waitResponse = new ConcurrentHashMap<>();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        String responseId = response.getId();
        Long lastTimestamp = waitResponse.get(responseId);
        if (response.getSuccess()) {
            logger.info("请求 {} 抢票成功, 订单号 {}, 耗时 {}", responseId, response.getOrderId(), currentTimeMillis - lastTimestamp);
        } else {
            logger.info("请求 {} 抢票失败, 因为 {}, 耗时 {}", responseId, response.getException(), currentTimeMillis - lastTimestamp);
        }
    }

    public void sendRequest(Request request) {
        channel.writeAndFlush(request);
    }

    public void add(Request request) {
        waitResponse.put(request.getId(), System.currentTimeMillis());
    }
}
