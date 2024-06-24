package kr.co.danal.naverworks.api.gateway.service;

import kr.co.danal.naverworks.api.gateway.model.Message;
import kr.co.danal.naverworks.api.gateway.model.MessageEvent;
import kr.co.danal.naverworks.api.gateway.model.ResponseData;
import kr.co.danal.naverworks.api.gateway.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageEventService {

    private final WebClient webClient;

    @Value("${channels.message.start:please enter}")
    private String startMessage;

    @Value("${channels.message.guide:write guide}")
    private String guideMessage;

    public Mono<ResponseEntity<ResponseData>> forwardRequest(MessageEvent messageEvent) {
        String uri = getUriByMessageEvent(messageEvent);

        return webClient.post()
                .uri(uri)
                .body(Mono.just(messageEvent), MessageEvent.class)
                .retrieve()
                .toEntity(ResponseData.class);
    }

    public String getUriByMessageEvent(MessageEvent messageEvent) {
        String[] message = MessageUtils.splitBySlash(messageEvent.getContent().getText());
        String type = MessageUtils.getPartByLocation(message, 0);
        String channel = MessageUtils.getPartByLocation(message, 1);
        String channelId = MessageUtils.getPartByLocation(message, 2);
        String uri = "";

        if (!isValidParams(type, channel, channelId)) {
            uri = "/channels/error";
            return uri;
        };

        switch (type) {
            case "start", "시작하기":
                uri = "channels/start";
                break;
            case "get":
                uri = "/channels/get";
                break;
            case "add":
                uri = kr.co.danal.naverworks.api.gateway.util.StringUtils.concat("/channels/add/", channel, "/", channelId);
                break;
            case "update":
                uri = kr.co.danal.naverworks.api.gateway.util.StringUtils.concat("/channels/update/", channel, "/", channelId);
                break;
            case "delete":
                uri = kr.co.danal.naverworks.api.gateway.util.StringUtils.concat("/channels/delete/", channel);
                break;
            case "guide":
                uri = "/channels/guide";
                break;
        }
        return uri;
    }

    public boolean isValidParams(String type, String channel, String channelId) {
        boolean isValid = true;
        switch (type) {
            case "start", "시작하기", "get", "guide":
                break;
            case "add", "update":
                if (StringUtils.isBlank(channel) ||
                        StringUtils.isBlank(channelId)) {
                    isValid = false;
                }
                break;
            case "delete":
                if (StringUtils.isBlank(channel)) {
                    isValid = false;
                }
                break;
            default:
                isValid = false;
        }
        return isValid;
    }

    public String getStartMessage() {
        return startMessage;
    }

    public String getGuideMessage() {
        return guideMessage;
    }

    public Mono<ResponseEntity<ResponseData>> sendMessage(MessageEvent messageEvent, String text) {
        String uri = "/message/channel/users/" + messageEvent.getSource().getUserId();
        Message message = Message.builder()
                .content(Message.Content.builder()
                        .type("text")
                        .text(text).build())
                .build();

        return webClient.post()
                .uri(uri)
                .body(Mono.just(message), Message.class)
                .retrieve()
                .toEntity(ResponseData.class);
    }
}