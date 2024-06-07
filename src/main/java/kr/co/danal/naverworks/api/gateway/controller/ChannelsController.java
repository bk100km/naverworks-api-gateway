package kr.co.danal.naverworks.api.gateway.controller;

import kr.co.danal.naverworks.api.gateway.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
@Slf4j
public class ChannelsController {

    private final ChannelService channelService;

    @GetMapping("")
    public Map<String, String> get() {
        return channelService.getChannelsReadOnly();
    }

    @RequestMapping(value = {"/add/{channel}", "/update/{channel}"}, method = RequestMethod.POST)
    public ResponseEntity<Object> add(
            @PathVariable String channel,
            @RequestBody String channelId) throws IOException {
        channelService.addOrUpdateChannel(channel, channelId);
        return new ResponseEntity<Object>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{channel}")
    public ResponseEntity<Object> delete(@PathVariable String channel) throws IOException {
        channelService.removeChannel(channel);
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
}