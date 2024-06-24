package kr.co.danal.naverworks.api.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class LoggingFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest originalRequest = exchange.getRequest();
        return DataBufferUtils.join(originalRequest.getBody())
                .defaultIfEmpty(new DefaultDataBufferFactory().wrap(new byte[0]))
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    String requestBody = new String(bytes, StandardCharsets.UTF_8);
                    log.info("Request: method={}, uri={}, headers={}, body={}",
                            originalRequest.getMethod(),
                            originalRequest.getURI(),
                            originalRequest.getHeaders(),
                            requestBody);
                    DataBuffer newDataBuffer = new DefaultDataBufferFactory().wrap(bytes);
                    ServerHttpRequest decoratedRequest = new RequestBodyDecorator(exchange, newDataBuffer);
                    ServerHttpResponse decoratedResponse = new ResponseBodyDecorator(exchange, requestBody);

                    return chain.filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build())
                            .doOnError(e -> log.error("Mutated LoggingFilter chain error!"));
                })
                .onErrorResume(ResponseStatusException.class, e -> {
                    log.error("LoggingFilter reactive stream error!", e);
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        ServerHttpResponse response = exchange.getResponse();
                        response.setStatusCode(HttpStatus.NOT_FOUND);
                        byte[] bytes = "404 Not Found".getBytes();
                        DataBuffer buffer = response.bufferFactory().wrap(bytes);
                        return response.writeWith(Mono.just(buffer));
                    }
                    return Mono.error(e);
                });
    }

    public class RequestBodyDecorator extends ServerHttpRequestDecorator {

        private final ServerWebExchange exchange;
        private final DataBuffer newDataBuffer;

        public RequestBodyDecorator(ServerWebExchange exchange, DataBuffer newDataBuffer) {
            super(exchange.getRequest());
            this.exchange = exchange;
            this.newDataBuffer = newDataBuffer;
        }

        @Override
        public Flux<DataBuffer> getBody() {
            return Flux.just(newDataBuffer);
        }
    }

    public class ResponseBodyDecorator extends ServerHttpResponseDecorator {

        private final ServerWebExchange exchange;
        private final String requestBody;

        public ResponseBodyDecorator(ServerWebExchange exchange, String requestBody) {
            super(exchange.getResponse());
            this.exchange = exchange;
            this.requestBody = requestBody;
        }

        @Override
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            ServerHttpRequest originalRequest = exchange.getRequest();
            ServerHttpResponse originalResponse = exchange.getResponse();

            return DataBufferUtils.join(body)
                    .flatMap(dataBuffer -> {
                        byte[] responseBytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(responseBytes);
                        DataBufferUtils.release(dataBuffer);
                        String responseBody = new String(responseBytes, StandardCharsets.UTF_8);
                        log.info("\n[One-line]\nRequest: method={}, uri={}, headers={}, body={}\nResponse: status={}, headers={}, body={}",
                                originalRequest.getMethod(),
                                originalRequest.getURI(),
                                originalRequest.getHeaders(),
                                requestBody,
                                originalResponse.getStatusCode(),
                                originalResponse.getHeaders(),
                                responseBody);

                        DataBuffer buffer = new DefaultDataBufferFactory().wrap(responseBytes);
                        return super.writeWith(Flux.just(buffer));
                    })
                    .doOnError(e -> {
                        log.error("Response body logging Error! ", e);
                    });
        }

        @Override
        public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
            return writeWith(Flux.from(body).flatMapSequential(p -> p));
        }
    }
}