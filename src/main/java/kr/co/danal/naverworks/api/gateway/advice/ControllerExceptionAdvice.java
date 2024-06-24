package kr.co.danal.naverworks.api.gateway.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@ControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception e, WebRequest request) {
        log.error("Internal Server Error: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred. Please try again later.");
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientResponseException(WebClientResponseException e) {
        log.error("WebClient Error: ", e);
        return ResponseEntity.status(e.getStatusCode()).body("An error occurred while processing your request. Please try again later.");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException e) {
        log.error("Response Status Error: ", e);
        return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
    }
}