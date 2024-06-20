package kr.co.danal.naverworks.api.gateway.controller;

import kr.co.danal.naverworks.api.gateway.util.ClientUtils;
import kr.co.danal.naverworks.api.gateway.service.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/directory/levels")
@RestController
public class LevelsController {

	private final JWTService jwtService;
	private final ClientUtils clientUtils;
	
	//직급 목록 조회
	@GetMapping("")
	public Mono<ResponseEntity<Object>> list() throws IOException, GeneralSecurityException  {
		return clientUtils.get(jwtService.getServerToken(), "https://www.worksapis.com/v1.0/directory/levels");
	}
	
	//직급 조회
	@GetMapping("/{levelId}")
	public Mono<ResponseEntity<Object>> get(
			@PathVariable("levelId") String levelId
			) throws IOException, GeneralSecurityException {
		return clientUtils.get(jwtService.getServerToken(), "https://www.worksapis.com/v1.0/directory/levels/" + levelId);
	}
}
