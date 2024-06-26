package kr.co.danal.naverworks.api.gateway.controller;

import kr.co.danal.naverworks.api.gateway.service.ChannelService;
import kr.co.danal.naverworks.api.gateway.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/message")
@RestController
public class MessageController {

	private final MessageService messageService;
	private final ChannelService channelService;

	//사용자 대상 메세지 전송
	@PostMapping("/{platform}/users/{userId}")
	public Mono<ResponseEntity<Object>> messageForUser(
			@PathVariable("platform") String platform,
			@PathVariable("userId") String userId,
			@RequestBody String text
	) {
		return messageService.sendMessage("user", platform, userId, text);
	}
	
	//채널 대상 메세지 전송
	@PostMapping("/{platform}/channels/{channel}")
	public Mono<ResponseEntity<Object>> messageForChannel(
			@PathVariable("platform") String platform,
			@PathVariable("channel") String channel,
			@RequestBody String text
	) {
		return messageService.sendMessage("channel", platform, channelService.getChannelIdByChannel(channel), text);
	}
}
