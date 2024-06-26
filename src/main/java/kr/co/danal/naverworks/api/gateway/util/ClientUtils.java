package kr.co.danal.naverworks.api.gateway.util;

import kr.co.danal.naverworks.api.gateway.service.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class ClientUtils {

	private final WebClient webClient;
	private final JWTService jwtService;
	
	/**
	 * get 방식으로 호출
	 * @param uri
	 * @return
	 */
	public Mono<ResponseEntity<Object>> get(String uri) {
		String accessToken = jwtService.getServerToken();
		return webClient.get()
				.uri(uri)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.retrieve()
				.onStatus(response -> response.isError(), this::handleErrorResponse)
				.toEntity(Object.class)
				.doOnNext(response -> log.info("\n[WebClient One-line]\nRequest: method=get, uri={}, token={}\nResponse: satus={}, headers={}, body={}",
						uri, accessToken, response.getStatusCode(), response.getHeaders(), response.getBody()))
				.doOnError(e -> log.error("\n[WebClient One-line]\nRequest: method=get, uri={}, token={}\nException={}",
						uri, accessToken, e.getMessage(), e))
				.flatMap(responseEntity -> Mono.just(new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK)));
	}
	
	/**
	 * post 형태로 데이터 전송
	 * @param uri
	 * @param data
	 * @return
	 */
	public Mono<ResponseEntity<Object>> post(String uri, Object data) {
		String accessToken = jwtService.getServerToken();
		return webClient.post()
				.uri(uri)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(data))
				.retrieve()
				.onStatus(response -> response.isError(), this::handleErrorResponse)
				.toEntity(Object.class)
				.doOnNext(response -> log.info("\n[WebClient One-line] \nRequest: method=post, uri={}, token={}, body={}\nResponse: satus={}, headers={}, body={}",
						uri, accessToken, data, response.getStatusCode(), response.getHeaders(), response.getBody()))
				.doOnError(e -> log.error("\n[WebClient One-line]\nRequest: method=post, uri={}, token={}, body={}\nException={}",
						uri, accessToken, data, e.getMessage(), e))
				.flatMap(responseEntity -> Mono.just(new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK)));
	}

	private Mono<? extends Throwable> handleErrorResponse(ClientResponse response) {
		return response.bodyToMono(String.class)
				.flatMap(errorMessage -> {
					HttpStatusCode status = response.statusCode();
					if (status.is4xxClientError()) {
						return Mono.error(new HttpClientErrorException(status, "Client Error: " + errorMessage));
					} else if (status.is5xxServerError()) {
						return Mono.error(new HttpServerErrorException(status, "Server Error: " + errorMessage));
					} else {
						return Mono.error(new RuntimeException("Unknown Error: " + errorMessage));
					}
				});
	}
}
