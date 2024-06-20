package kr.co.danal.naverworks.api.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelService {

    private Map<String, String> channels;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${channels.file.path:src/main/resources/channels.json}")
    private String channelsFilePath;

    @PostConstruct
    public void loadChannels() throws FileNotFoundException {
        File file = new File(channelsFilePath);
        if (file.exists()) {
            try {
                channels = objectMapper.readValue(file, Map.class);
                log.info("Channels loaded successfully. - {}", channels);
            } catch (IOException e) {
                log.error("Load channels failed!", e);
            }
        } else {
            throw new FileNotFoundException("channels.json file not found! : " + channelsFilePath);
        }
    }

    @PreDestroy
    public void saveChannels() throws IOException {
        try {
            File file = new File(channelsFilePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            objectMapper.writeValue(file, channels);
        } catch (IOException e) {
            log.error("Failed to save channels!", e);
            throw e;
        }
    }

    public String getChannelIdByChannel(String channel) {
        return channels.get(channel);
    }

    public void addOrUpdateChannel(String channel, String channelId) throws IOException {
        channels.put(channel, channelId);
        saveChannels();
    }

    public void removeChannel(String channelId) throws IOException {
        channels.remove(channelId);
        saveChannels();
    }

    public String getChannelsToPrettyJson() throws JsonProcessingException {
        StringJoiner joiner = new StringJoiner(",\n", "{\n", "\n}");
        channels.forEach((key, value) -> joiner.add("  \"" + key + "\": \"" + value + "\""));
        return joiner.toString();
    }

    public Map<String, String> getChannels() {
        return channels;
    }
}