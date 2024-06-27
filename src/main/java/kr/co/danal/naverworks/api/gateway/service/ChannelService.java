package kr.co.danal.naverworks.api.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import kr.co.danal.naverworks.api.gateway.model.MessageEvent;
import kr.co.danal.naverworks.api.gateway.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ChannelBotService channelBotService;

    private ConcurrentHashMap<String, String> channels;

    @Value("${channels.file.path:src/main/resources/channels.json}")
    private String channelsFilePath;

    @PostConstruct
    public void loadChannels() throws FileNotFoundException {
        File file = new File(channelsFilePath);
        if (file.exists()) {
            try {
                channels = objectMapper.readValue(file, ConcurrentHashMap.class);
                log.info("Channels loaded successfully. - {}", channels);
            } catch (IOException e) {
                log.error("Load channels failed!", e);
            }
        } else {
            throw new FileNotFoundException("channels.json file not found! : " + channelsFilePath);
        }
    }

    @PreDestroy
    public void saveChannels() {
        saveChannels(null);
    }

    @Synchronized
    public void saveChannels(String channel) {
        log.info("Write channels to file. channels={}", channels);
        try {
            File file = new File(channelsFilePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            objectMapper.writeValue(file, channels);
        } catch (IOException e) {
            channels.remove(channel);
            throw new RuntimeException("Failed to save channels because of file I/O.", e);
        }
    }

    public String getChannelIdByChannel(String channel) {
        return channels.get(channel);
    }

    public Mono<ResponseEntity<Object>> addChannel(MessageEvent messageEvent, String channel, String channelId) {
        if (channels.containsKey(channel)) {
            messageEvent.setAdditionalText(StringUtils.concat("Failed! ", channel, " already exists."));
        } else {
            channels.put(channel, channelId);
            saveChannels(channel);
            messageEvent.setAdditionalText(StringUtils.concat("Success! ", channel, " registration completed."));
        }
        return channelBotService.sendBotResponseMessage(messageEvent, messageEvent.getAdditionalText());
    }

    public Mono<ResponseEntity<Object>> updateChannel(MessageEvent messageEvent, String channel, String channelId) {
        if (!channels.containsKey(channel)) {
            messageEvent.setAdditionalText(StringUtils.concat("Failed! ", channel, " doesn't exist."));
        } else {
            channels.put(channel, channelId);
            saveChannels(channel);
            messageEvent.setAdditionalText(StringUtils.concat("Success! ", channel, " registration completed."));
        }
        return channelBotService.sendBotResponseMessage(messageEvent, messageEvent.getAdditionalText());
    }

    public Mono<ResponseEntity<Object>> removeChannel(MessageEvent messageEvent, String channel) {
        if (!channels.containsKey(channel)) {
            messageEvent.setAdditionalText(StringUtils.concat("Failed! ", channel, " doesn't exist."));
        } else {
            channels.remove(channel);
            saveChannels(channel);
            messageEvent.setAdditionalText(StringUtils.concat("Success! ", channel, " deletion completed."));
        }
        return channelBotService.sendBotResponseMessage(messageEvent, messageEvent.getAdditionalText());
    }

    public String getChannelsToPrettyJson() {
        StringJoiner joiner = new StringJoiner(",\n", "{\n", "\n}");
        channels.forEach((key, value) -> joiner.add("  \"" + key + "\": \"" + value + "\""));
        return joiner.toString();
    }

    public Map<String, String> getChannels() {
        return channels;
    }
}