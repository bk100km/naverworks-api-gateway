package kr.co.danal.naverworks.api.gateway.service;

import kr.co.danal.naverworks.api.gateway.config.BotConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotService {

    private final BotConfig botConfig;

    public String getBotIdbyPlatform(String platform) {
        return botConfig.getIds().getOrDefault(platform, null);
    }
}