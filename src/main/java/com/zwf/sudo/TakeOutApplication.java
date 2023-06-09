package com.zwf.sudo;



import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@Slf4j
@ServletComponentScan
@EnableTransactionManagement
@EnableCaching
public class TakeOutApplication {
    public static void main(String[] args) {
        SpringApplication.run(TakeOutApplication.class,args);
        log.info("项目启动了......");
    }
}
