package kr.co.danal.naverworks.api.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "positions")
public class PositionsConfig {
    private List<String> managers;
}