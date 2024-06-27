package kr.co.danal.naverworks.api.gateway.controller;

import kr.co.danal.naverworks.api.gateway.model.MessageEvent;
import kr.co.danal.naverworks.api.gateway.service.ChannelService;
import kr.co.danal.naverworks.api.gateway.service.ChannelBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channels")
@Slf4j
public class ChannelsController {

    private final ChannelService channelService;
    private final ChannelBotService channelBotService;

    @PostMapping("")
    public Mono<ResponseEntity<Object>> handle(
            @RequestBody MessageEvent messageEvent) {
        return channelBotService.forwardRequest(messageEvent);
    }

    @PostMapping("/start")
    public Mono<ResponseEntity<Object>> start(
            @RequestBody MessageEvent messageEvent) {
        return channelBotService.sendBotResponseMessage(messageEvent, channelBotService.getStartMessage());
    }

    @PostMapping("/get")
    public Mono<ResponseEntity<Object>> get(
            @RequestBody MessageEvent messageEvent) {
        return channelBotService.sendBotResponseMessage(messageEvent, channelService.getChannelsToPrettyJson());
    }

    @RequestMapping(value = {"/add/{channel}/{channelId}"}, method = RequestMethod.POST)
    public Mono<ResponseEntity<Object>> add(
            @RequestBody MessageEvent messageEvent,
            @PathVariable String channel,
            @PathVariable String channelId) {
        return channelService.addChannel(messageEvent, channel, channelId);
    }

    @RequestMapping(value = {"/update/{channel}/{channelId}"}, method = RequestMethod.POST)
    public Mono<ResponseEntity<Object>> update(
            @RequestBody MessageEvent messageEvent,
            @PathVariable String channel,
            @PathVariable String channelId) {
        return channelService.updateChannel(messageEvent, channel, channelId);
    }

    @PostMapping("/delete/{channel}")
    public Mono<ResponseEntity<Object>> delete(
            @RequestBody MessageEvent messageEvent,
            @PathVariable String channel) {
        return channelService.removeChannel(messageEvent, channel);
    }

    @PostMapping("/error")
    public Mono<ResponseEntity<Object>> error(
            @RequestBody MessageEvent messageEvent) {
        return channelBotService.sendBotResponseMessage(messageEvent, messageEvent.getAdditionalText());
    }

    @PostMapping("/guide")
    public Mono<ResponseEntity<Object>> guide(
            @RequestBody MessageEvent messageEvent) {
        return channelBotService.sendBotResponseMessage(messageEvent, channelBotService.getGuideMessage());
    }
}