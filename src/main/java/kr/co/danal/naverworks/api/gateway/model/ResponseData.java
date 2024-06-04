package kr.co.danal.naverworks.api.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpHeaders;

@Getter
@AllArgsConstructor
@Builder
public class ResponseData {

	private int status;
	private String message;
	private HttpHeaders httpHeaders;
	private Object data;
	
}
