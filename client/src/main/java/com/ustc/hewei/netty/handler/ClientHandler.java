package com.ustc.hewei.netty.handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import warp.Request;
import warp.Response;

import java.util.concurrent.CountDownLatch;

/**
 * @author hewei
 * @version 1.0
 * @description: 短连接handler
 * @date 2022/10/21 15:59
 */

public class ClientHandler extends SimpleChannelInboundHandler<Response> {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Request request;
    private final CountDownLatch latch;
    private Response response = null;

    public ClientHandler(Request request) {
        this.request = request;
        latch = new CountDownLatch(1);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(request);
    }

    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        this.response = response;
        if (response.getSuccess()) {
            logger.info("请求 {} 抢票成功, 订单号 {}", response.getId(), response.getOrderId());
        } else {
            logger.info("请求 {} 抢票失败, 因为 {}", response.getId(), response.getException());
        }
        latch.countDown();
    }

    public Response getResponse() throws InterruptedException {
        latch.await();
        return response;
    }
}
