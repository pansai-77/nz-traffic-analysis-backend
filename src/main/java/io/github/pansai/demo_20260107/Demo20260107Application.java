package io.github.pansai.demo_20260107;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("io.github.pansai.demo_20260107.mapper")
@SpringBootApplication
public class Demo20260107Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo20260107Application.class, args);
    }

}
