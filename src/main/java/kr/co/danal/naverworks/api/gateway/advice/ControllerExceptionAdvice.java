package kr.co.danal.naverworks.api.gateway.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@ControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<String>> handleAllExceptions(Exception e, ServerWebExchange exchange) {
        return Mono.just(new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public Mono<ResponseEntity<String>> handleHttpServerErrorExceptions(HttpServerErrorException e, ServerWebExchange exchange) {
        return Mono.just(new ResponseEntity<>(e.getMessage(), e.getStatusCode()));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public Mono<ResponseEntity<String>> handleHttpClientErrorExceptions(HttpClientErrorException e, ServerWebExchange exchange) {
        return Mono.just(new ResponseEntity<>(e.getMessage(), e.getStatusCode()));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<String>> handleWebClientResponseException(WebClientResponseException e, ServerWebExchange exchange) {
        return Mono.just(new ResponseEntity<>(e.getMessage(), e.getStatusCode()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<String>> handleResponseStatusException(ResponseStatusException e, ServerWebExchange exchange) {
        return Mono.just(new ResponseEntity<>(e.getMessage(), e.getStatusCode()));
    }
}