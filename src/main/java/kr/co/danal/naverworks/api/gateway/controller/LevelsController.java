package kr.co.danal.naverworks.api.gateway.controller;

import kr.co.danal.naverworks.api.gateway.model.ResponseData;
import kr.co.danal.naverworks.api.gateway.util.ClientUtils;
import kr.co.danal.naverworks.api.gateway.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@RequestMapping("/directory/levels")
@RestController
public class LevelsController {

	private final JWTService service;
	private final ClientUtils clientUtils;
	
	//직급 목록 조회
	@GetMapping("")
	public ResponseEntity<Object> list() throws IOException, GeneralSecurityException  {
		ResponseData responseData = clientUtils.get(service.getServerToken(), "https://www.worksapis.com/v1.0/directory/levels");
		Map<String, Object> dataMap = new ObjectMapper().convertValue(responseData.getData(), Map.class);
		return new ResponseEntity<Object>(dataMap, HttpStatus.OK); 
	}
	
	//직급 조회
	@GetMapping("/{levelId}")
	public ResponseEntity<Object> get(
			@PathVariable("levelId") String levelId
			) throws IOException, GeneralSecurityException {
		ResponseData responseData = clientUtils.get(service.getServerToken(), "https://www.worksapis.com/v1.0/directory/levels/" + levelId);
		Map<String, Object> dataMap = new ObjectMapper().convertValue(responseData.getData(), Map.class);
		return new ResponseEntity<Object>(dataMap, HttpStatus.OK);
	}
}
