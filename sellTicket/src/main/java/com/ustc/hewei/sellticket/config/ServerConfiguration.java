package com.ustc.hewei.sellticket.config;

import com.ustc.hewei.sellticket.netty.NettyServer;
import com.ustc.hewei.sellticket.netty.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hewei
 * @version 1.0
 * @description: 用配置类来开启服务
 * @date 2022/10/21 15:48
 */

@Configuration
public class ServerConfiguration {

    @Bean
    public Server server() {
        NettyServer nettyServer = new NettyServer();
        nettyServer.start();
        return nettyServer;
    }
}
