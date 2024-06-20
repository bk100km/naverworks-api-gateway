package kr.co.danal.naverworks.api.gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.danal.naverworks.api.gateway.model.MessageEvent;
import kr.co.danal.naverworks.api.gateway.model.ResponseData;
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
    public Mono<ResponseEntity<ResponseData>> handle(@RequestBody MessageEvent messageEvent) {
        return messageEventService.forwardRequest(messageEvent);
    }

    @PostMapping("/start")
    public Mono<ResponseEntity<ResponseData>> start(@RequestBody MessageEvent messageEvent) {
        return messageEventService.sendMessage(messageEvent, messageEventService.getStartMessage());
    }

    @PostMapping("/get")
    public Mono<ResponseEntity<ResponseData>> get(@RequestBody MessageEvent messageEvent) throws JsonProcessingException {
        return messageEventService.sendMessage(messageEvent, channelService.getChannelsToPrettyJson());
    }

    @RequestMapping(value = {"/add/{channel}/{channelId}", "/update/{channel}/{channelId}"}, method = RequestMethod.POST)
    public Mono<ResponseEntity<ResponseData>> add(
            @RequestBody MessageEvent messageEvent,
            @PathVariable String channel,
            @PathVariable String channelId) throws IOException {
        channelService.addOrUpdateChannel(channel, channelId);
        return messageEventService.sendMessage(messageEvent, "success");
    }

    @PostMapping("/delete/{channel}")
    public Mono<ResponseEntity<ResponseData>> delete(
            @RequestBody MessageEvent messageEvent,
            @PathVariable String channel) throws IOException {
        channelService.removeChannel(channel);
        return messageEventService.sendMessage(messageEvent, "success");
    }

    @PostMapping("/error")
    public Mono<ResponseEntity<ResponseData>> error(
            @RequestBody MessageEvent messageEvent) throws IOException {
        return messageEventService.sendMessage(messageEvent, "fail");
    }

    @PostMapping("/guide")
    public Mono<ResponseEntity<ResponseData>> guide(@RequestBody MessageEvent messageEvent) {
        return messageEventService.sendMessage(messageEvent, messageEventService.getGuideMessage());
    }
}