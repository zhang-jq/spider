package com.zjq.spider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.zjq.spider.dao")
public class SpiderApplication {

    public static void main(String[] args) throws Exception {
//        SpringApplication.run(SpiderApplication.class, args);

    }

}
