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

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/positions")
@RestController
public class PositionsController {

	private final ClientUtils clientUtils;
	
	//직책 목록 조회
	@GetMapping("")
	public Mono<ResponseEntity<Object>> list() {
		return clientUtils.get("https://www.worksapis.com/v1.0/directory/positions");
	}

	//직책 조회
	@GetMapping("/{positionId}")
	public Mono<ResponseEntity<Object>> get(
			@PathVariable("positionId") String positionId
	) {
		return clientUtils.get("https://www.worksapis.com/v1.0/directory/positions/" + positionId);
	}
}
