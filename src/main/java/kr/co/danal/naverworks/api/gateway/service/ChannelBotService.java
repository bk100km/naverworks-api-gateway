package kr.co.danal.naverworks.api.gateway.service;

import kr.co.danal.naverworks.api.gateway.model.MessageEvent;
import kr.co.danal.naverworks.api.gateway.util.ClientUtils;
import kr.co.danal.naverworks.api.gateway.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelBotService {

    private final ClientUtils clientUtils;
    private final PositionsService positionsService;
    private final MessageService messageService;

    private final String MESSAGE_TYPE = "user";
    private final String MESSAGE_PLATFORM = "channelbot";

    @Value("${channels.message.start:please enter}")
    private String startMessage;

    @Value("${channels.message.guide:write guide}")
    private String guideMessage;

    public Mono<ResponseEntity<Object>> forwardRequest(MessageEvent messageEvent) {
        String uri = getUriByMessageEvent(messageEvent);
        return clientUtils.post(uri, messageEvent);
    }

    public String getUriByMessageEvent(MessageEvent messageEvent) {
        String[] message = MessageUtils.splitBySlash(messageEvent.getContent().getText());
        String type = MessageUtils.getPartByLocation(message, 0);
        String channel = MessageUtils.getPartByLocation(message, 1);
        String channelId = MessageUtils.getPartByLocation(message, 2);
        String uri;
        String userId = messageEvent.getSource().getUserId();

        if (!isValidParams(type, channel, channelId)) {
            log.error("Invalid parameters! type={}, channel={}, channelId={}", type, channel, channelId);
            uri = "/channels/error";
            messageEvent.setAdditionalText("Invalid parameters! Please check the command.");
            return uri;
        };

        if (!isValidPermission(type, userId)) {
            log.error("Invalid permission! type={}, userID={}", type, userId);
            uri = "/channels/error";
            messageEvent.setAdditionalText("Invalid permission! Please ask the manager level.");
            return uri;
        };

        if (StringUtils.equals(type, "add") && !isValidChannelId(channelId)) {
            log.error("Invalid channelId! channelId={}", channelId);
            uri = "/channels/error";
            messageEvent.setAdditionalText("Invalid channel ID! Please check the channel ID.");
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
            default:
                uri = "/channels/error";
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

    public boolean isValidPermission(String type, String userId) {
        boolean isValid;
        switch (type) {
            case "start", "시작하기", "get", "guide":
                isValid = true;
                break;
            case "add", "update", "delete":
                isValid = positionsService.isManagerPosition(userId);
                break;
            default:
                isValid = false;
        }
        return isValid;
    }

    public boolean isValidChannelId(String channelId) {
        boolean result = true;
        String testMessage = "Channel registration test.";
        HttpStatusCode status = messageService.sendMessageByChannelId("channelbot", channelId, testMessage).block().getStatusCode();
        if (status.is4xxClientError()) {
            result = false;
        }
        return result;
    }

    public String getStartMessage() {
        return startMessage;
    }

    public String getGuideMessage() {
        return guideMessage;
    }

    public Mono<ResponseEntity<Object>> sendBotResponseMessage(MessageEvent messageEvent, String text) {
        return messageService.sendMessage(MESSAGE_TYPE, MESSAGE_PLATFORM, messageEvent.getSource().getUserId(), text);
    }
}