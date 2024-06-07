package kr.co.danal.naverworks.api.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ChannelService {

    private Map<String, String> channels;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${channels.file.path:channels.json}")
    private String channelsFilePath;

    @PostConstruct
    public void loadChannels() throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("channels.json").getFile());

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
            objectMapper.writeValue(new File(channelsFilePath), channels);
        } catch (IOException e) {
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

    public void removeChannel(String channelId) {
        channels.remove(channelId);
    }

    public Map<String, String> getChannelsReadOnly() {
        return new ConcurrentHashMap<>(channels);
    }

    public Map<String, String> getChannels() {
        return channels;
    }
}