package com.mojian;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFileStorage
@MapperScan("com.mojian.mapper")
public class NeatAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(NeatAdminApplication.class, args);
        System.out.println("系统启动成功！");
    }
}
