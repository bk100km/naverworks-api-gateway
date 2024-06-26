package kr.co.danal.naverworks.api.gateway.service;

import kr.co.danal.naverworks.api.gateway.config.BotConfig;
import kr.co.danal.naverworks.api.gateway.model.Message;
import kr.co.danal.naverworks.api.gateway.util.ClientUtils;
import kr.co.danal.naverworks.api.gateway.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final ChannelService channelService;
    private final ClientUtils clientUtils;
    private final BotConfig botConfig;

    @Value("${url.naverworks:https://www.worksapis.com/v1.0}")
    private String naverworksUrl;

    public Mono<ResponseEntity<Object>> sendMessage(String type, String platform, String target, Message message) {
        String url = "";
        String botId = botConfig.getBotIdbyPlatform(platform);

        switch (type) {
            case "user":
                url = StringUtils.concat(naverworksUrl, "/bots/", botId, "/users/", target, "/messages");
                break;
            case "channel":
                String channelId = channelService.getChannelIdByChannel(target);
                url = StringUtils.concat(naverworksUrl, "/bots/", botId, "/channels/", channelId, "/messages");
                break;
        }
        return clientUtils.post(url, message);
    }
}