package com.ustc.hewei.sellticket.netty;

import codec.CommonDecode;
import codec.CommonEncode;
import com.ustc.hewei.sellticket.netty.handler.RequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import warp.Request;
import warp.Response;

import javax.annotation.Resource;
import java.util.concurrent.*;

/**
 * @author hewei
 * @version 1.0
 * @description: 服务器端
 * @date 2022/10/21 15:03
 */

public class NettyServer implements Server {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    @Resource
    private RequestHandler requestHandler;
    private Thread thread;
    private final ExecutorService executorService;

    public NettyServer() {
        executorService = new ThreadPoolExecutor(400, 400, 1000,TimeUnit.MILLISECONDS
                , new ArrayBlockingQueue<>(4000, true), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public void start() {
        thread = new Thread(() -> {
            NioEventLoopGroup boosGroup = new NioEventLoopGroup();
            NioEventLoopGroup workGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(boosGroup, workGroup).channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 500)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline pipeline = socketChannel.pipeline();
                                pipeline.addLast(new CommonDecode())
                                        .addLast(new CommonEncode())
                                        .addLast(new ServerHandler());
                            }
                        });
                ChannelFuture channelFuture = bootstrap.bind(6666).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                logger.error("远程服务停止");
            } finally {
                workGroup.shutdownGracefully();
                boosGroup.shutdownGracefully();
            }
        });
        thread.start();
    }

    @Override
    public void stop() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }

    private class ServerHandler extends SimpleChannelInboundHandler<Request> {

        //private final DefaultEventLoopGroup eventExecutors = new DefaultEventLoopGroup(2000);

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
            logger.info("收到请求");
            executorService.execute(() -> {
                Response response = requestHandler.handlerRequest(request);
                ctx.writeAndFlush(response);
            });
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            throw new RuntimeException("服务端处理器异常", cause.getCause());
        }
    }
}
