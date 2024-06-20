package kr.co.danal.naverworks.api.gateway.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.NameValuePair;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class ClientUtils {

	private final WebClient webClient;
	
	/**
	 * get 방식으로 호출
	 * @param accessToken
	 * @param uri
	 * @return
	 */
	public Mono<ResponseEntity<Object>> get(String accessToken, String uri) {
		log.info("accessToken={}, uri={}", accessToken, uri);
		return webClient.get()
				.uri(uri)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.retrieve()
				.onStatus(response -> response.isError(), this::handleErrorResponse)
				.toEntity(Object.class)
				.doOnSuccess(content -> log.info("Content={}", content))
				.doOnError(e -> log.error("Exception={}", e.getMessage(), e))
				.flatMap(responseEntity -> Mono.just(new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK)));
	}
	
	/**
	 * post 형태로 데이터 전송
	 * @param accessToken
	 * @param uri
	 * @param data
	 * @return
	 */
	public Mono<ResponseEntity<Object>> post(String accessToken, String uri, Object data) {
		log.info("accessToken={}, uri={}, data={}", accessToken, uri, data);
		return webClient.post()
				.uri(uri)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(data))
				.retrieve()
				.onStatus(response -> response.isError(), this::handleErrorResponse)
				.toEntity(Object.class)
				.doOnSuccess(content -> log.info("Content={}", content))
				.doOnError(e -> log.error("Exception={}", e.getMessage(), e))
				.flatMap(responseEntity -> Mono.just(new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK)));
	}

	/**
	 * Post 전송 - FormUrlencoded 형, accesstoken 발급 받을 때 사용함
	 * @param uri
	 * @param params
	 * @return
	 */
	public Mono<String> postByFormUrlencoded(String uri, List<NameValuePair> params) {
		log.info("uri={}, params={}", uri, params);
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		for (NameValuePair param : params) {
			formData.add(param.getName(), param.getValue());
		}

		return webClient.post()
				.uri(uri)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.body(BodyInserters.fromFormData(formData))
				.retrieve()
				.onStatus(response -> response.isError(), this::handleErrorResponse)
				.bodyToMono(String.class)
				.doOnSuccess(content -> log.info("Content={}", content))
				.doOnError(e -> log.error("Exception={}", e.getMessage(), e));
	}

	private Mono<? extends Throwable> handleErrorResponse(ClientResponse response) {
		return response.bodyToMono(String.class)
				.flatMap(errorMessage -> {
					HttpStatusCode status = response.statusCode();
					if (status.is4xxClientError()) {
						return Mono.error(new RuntimeException("Client Error: " + errorMessage));
					} else if (status.is5xxServerError()) {
						return Mono.error(new RuntimeException("Server Error: " + errorMessage));
					} else {
						return Mono.error(new RuntimeException("Unknown Error: " + errorMessage));
					}
				});
	}
	
}
