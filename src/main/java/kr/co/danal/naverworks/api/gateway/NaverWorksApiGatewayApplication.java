package kr.co.danal.naverworks.api.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class NaverWorksApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(NaverWorksApiGatewayApplication.class, args);
        log.info("NaverWorks API Gateway is running!");
    }
}