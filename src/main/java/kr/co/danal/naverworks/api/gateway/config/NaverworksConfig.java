package kr.co.danal.naverworks.api.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "naverworks")
public class NaverworksConfig {
    private String url;
}