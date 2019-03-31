package com.mee;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;



@SpringBootApplication
@ComponentScan(basePackages = "com.mee.manage")
@MapperScan(basePackages = {"com.mee.manage.mapper"})
public class MeeManageApplication {


    public static void main(String[] args) {

        SpringApplication.run(MeeManageApplication.class, args);

    }

}
