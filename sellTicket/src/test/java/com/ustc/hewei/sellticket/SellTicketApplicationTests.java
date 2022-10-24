package com.ustc.hewei.sellticket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

@SpringBootTest
class SellTicketApplicationTests {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void addUser() throws IOException {
        File file = new File("/Users/hewei/java-project/byteDanceProject01/client/src/main/resources/tokens.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
        accessFile.seek(accessFile.length());
        for (int i = 0; i < 1000000; i++) {
            String token = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set("user:token:" + token, String.valueOf(i + 1));
            accessFile.write(token.getBytes());
            accessFile.write("\r\n".getBytes());
        }
    }
}
