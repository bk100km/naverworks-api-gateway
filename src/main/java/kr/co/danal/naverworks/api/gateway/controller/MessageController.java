package kr.co.danal.naverworks.api.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.danal.naverworks.api.gateway.config.NaverworksConfig;
import kr.co.danal.naverworks.api.gateway.model.Message;
import kr.co.danal.naverworks.api.gateway.model.ResponseData;
import kr.co.danal.naverworks.api.gateway.service.BotService;
import kr.co.danal.naverworks.api.gateway.service.ChannelService;
import kr.co.danal.naverworks.api.gateway.service.JWTService;
import kr.co.danal.naverworks.api.gateway.util.StringUtils;
import kr.co.danal.naverworks.api.gateway.util.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/bots/messages")
@RestController
public class MessageController {

	private final JWTService jwtService;
	private final BotService botService;
	private final ChannelService channelService;
	private final ClientUtils clientUtils;
	private final NaverworksConfig naverworksConfig;

	//사용자 대상 메세지 전송
	@PostMapping("/{platform}/users/{userId}")
	public ResponseEntity<Object> messageForUser(
			@PathVariable("platform") String platform,
			@PathVariable("userId") String userId,
			@RequestBody Message message
	) throws GeneralSecurityException, IOException {
		ResponseData responseData = clientUtils.post(jwtService.getServerToken(), StringUtils.concatThreadSafe(naverworksConfig.getUrl(), "/bots/", botService.getBotIdbyPlatform(platform), "/users/", userId, "/messages"), message);
		Object data = responseData.getData();

		Map<String, Object> dataMap = new ObjectMapper().convertValue(data, Map.class);

		return new ResponseEntity<Object>(dataMap, HttpStatus.OK);
	}
	
	//채널 대상 메세지 전송
	@PostMapping("/{platform}/channels/{channel}")
	public ResponseEntity<Object> messageForChannel(
			@PathVariable("platform") String platform,
			@PathVariable("channel") String channel,
			@RequestBody Message message
	) throws GeneralSecurityException, IOException {
		ResponseData responseData = clientUtils.post(jwtService.getServerToken(), StringUtils.concatThreadSafe(naverworksConfig.getUrl(), "/bots/", botService.getBotIdbyPlatform(platform), "/channels/", channelService.getChannelIdByChannel(channel), "/messages"), message);
		Object data = responseData.getData();

		Map<String, Object> dataMap = new ObjectMapper().convertValue(data, Map.class);

		return new ResponseEntity<Object>(dataMap, HttpStatus.OK);
	}
}
