package com.zykj.pointsmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author tang
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class PointsMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(PointsMallApplication.class, args);
    }

}
