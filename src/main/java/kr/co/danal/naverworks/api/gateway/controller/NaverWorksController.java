package kr.co.danal.naverworks.api.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.danal.naverworks.api.gateway.model.ResponseData;
import kr.co.danal.naverworks.api.gateway.service.JWTService;
import kr.co.danal.naverworks.api.gateway.util.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class NaverWorksController {

    private final JWTService service;
    private final ClientUtils utils;

    @GetMapping("/test")
    public String test() {
        log.info("Test endpoint called");
        return "test GetMapping!";
    }

    @GetMapping("/test2")
    public ResponseEntity<Object> list() throws IOException, GeneralSecurityException {

        ResponseData responseData = utils.get(service.getServerToken(), "https://www.worksapis.com/v1.0/directory/levels");
        Object data = responseData.getData();

        Map<String, Object> dataMap = new ObjectMapper().convertValue(data, Map.class);


        return new ResponseEntity<Object>(dataMap, HttpStatus.OK);
    }
}