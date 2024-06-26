package kr.co.danal.naverworks.api.gateway.controller;

import kr.co.danal.naverworks.api.gateway.util.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UsersController {

	private final ClientUtils clientUtils;
	
	//구성원 목록 조회
	@GetMapping("")
	public Mono<ResponseEntity<Object>> list() {
		return clientUtils.get("https://www.worksapis.com/v1.0/users");
	}

	//구성원 조회
	@GetMapping("/{userId}")
	public Mono<ResponseEntity<Object>> get(
			@PathVariable("userId") String userId
	) {
		return clientUtils.get("https://www.worksapis.com/v1.0/users/" + userId);
	}
}
