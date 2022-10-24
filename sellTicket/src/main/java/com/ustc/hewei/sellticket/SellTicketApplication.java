package com.ustc.hewei.sellticket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ustc.hewei.sellticket.mapper")
public class SellTicketApplication {

    public static void main(String[] args) {
        SpringApplication.run(SellTicketApplication.class, args);
    }

}
