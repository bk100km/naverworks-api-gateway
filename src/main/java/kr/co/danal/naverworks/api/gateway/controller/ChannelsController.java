package kr.co.danal.naverworks.api.gateway.controller;

import kr.co.danal.naverworks.api.gateway.model.MessageEvent;
import kr.co.danal.naverworks.api.gateway.service.ChannelService;
import kr.co.danal.naverworks.api.gateway.service.MessageEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channels")
@Slf4j
public class ChannelsController {

    private final ChannelService channelService;
    private final MessageEventService messageEventService;

    @PostMapping("")
    public Mono<ResponseEntity<String>> handle(@RequestBody MessageEvent messageEvent) {
        return messageEventService.forwardRequest(messageEvent);
    }

    @PostMapping("/start")
    public Mono<ResponseEntity<String>> start(@RequestBody MessageEvent messageEvent) {
        return messageEventService.sendMessage(messageEvent, messageEventService.getStartMessage());
    }

    @PostMapping("/get")
    public Mono<ResponseEntity<String>> get(@RequestBody MessageEvent messageEvent) {
        return messageEventService.sendMessage(messageEvent, channelService.getChannelsReadOnly().toString());
    }

    @RequestMapping(value = {"/add/{channel}/{channelId}", "/update/{channel}/{channelId}"}, method = RequestMethod.POST)
    public Mono<ResponseEntity<String>> add(
            @RequestBody MessageEvent messageEvent,
            @PathVariable String channel,
            @PathVariable String channelId) throws IOException {
        channelService.addOrUpdateChannel(channel, channelId);
        return messageEventService.sendMessage(messageEvent, "success");
    }

    @PostMapping("/delete/{channel}")
    public Mono<ResponseEntity<String>> delete(
            @RequestBody MessageEvent messageEvent,
            @PathVariable String channel) throws IOException {
        channelService.removeChannel(channel);
        return messageEventService.sendMessage(messageEvent, "success");
    }

    @PostMapping("/error")
    public Mono<ResponseEntity<String>> error(
            @RequestBody MessageEvent messageEvent) throws IOException {
        return messageEventService.sendMessage(messageEvent, "failed");
    }
}