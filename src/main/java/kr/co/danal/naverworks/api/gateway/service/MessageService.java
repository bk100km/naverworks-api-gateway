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

    private final ClientUtils clientUtils;
    private final BotConfig botConfig;

    @Value("${url.naverworks:https://www.worksapis.com/v1.0}")
    private String naverworksUrl;

    public Mono<ResponseEntity<Object>> sendMessage(String type, String platform, String target, String text) {
        String url = "";
        String botId = botConfig.getBotIdbyPlatform(platform);
        Message message = Message.builder()
                .content(Message.Content.builder()
                        .type("text")
                        .text(text).build())
                .build();

        switch (type) {
            case "user":
                url = StringUtils.concat(naverworksUrl, "/bots/", botId, "/users/", target, "/messages");
                break;
            case "channel":
                url = StringUtils.concat(naverworksUrl, "/bots/", botId, "/channels/", target, "/messages");
                break;
        }
        return clientUtils.post(url, message);
    }

    public Mono<ResponseEntity<Object>> sendMessageByChannelId(String platform, String channelId, String text) {
        String botId = botConfig.getBotIdbyPlatform(platform);
        Message message = Message.builder()
                .content(Message.Content.builder()
                        .type("text")
                        .text(text).build())
                .build();
        String url = StringUtils.concat(naverworksUrl, "/bots/", botId, "/channels/", channelId, "/messages");
        return clientUtils.post(url, message);
    }
}