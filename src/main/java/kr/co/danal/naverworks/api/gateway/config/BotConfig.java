package kr.co.danal.naverworks.api.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "bot")
public class BotConfig {

    private String privateKey;
    private String clientId;
    private String clientSecret;
    private String serviceAccount;
    private String scope;
    private Map<String, String> ids;
}