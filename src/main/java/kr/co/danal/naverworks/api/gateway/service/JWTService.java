package kr.co.danal.naverworks.api.gateway.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import kr.co.danal.naverworks.api.gateway.config.BotConfig;
import kr.co.danal.naverworks.api.gateway.model.TokenInfo;
import kr.co.danal.naverworks.api.gateway.util.ClientUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class JWTService {

	private final ClientUtils worksClient;
	private final BotConfig botConfig;
	private final Duration gracePeriod = Duration.ofMinutes(60); // NaverWorks 토큰 유효 기간(24시간)을 고려하여 1시간으로 설정
	private final ConcurrentHashMap<String, String> tokenCache = new ConcurrentHashMap<>();
	private Date tokenExpirationDate;

	@PostConstruct
	public synchronized void init() {
		// Application 시작 시 토큰을 미리 받아올 수 있음
		refreshToken();
	}

	public String getServerToken() throws IOException, GeneralSecurityException {
		if (tokenExpirationDate != null && new Date().before(tokenExpirationDate)) {
			// 토큰이 유효하다면 캐시된 토큰 반환
			return tokenCache.get("access_token");
		} else {
			// 토큰이 만료되었거나 존재하지 않으면 새로 발급받음
			return refreshToken();
		}
	}

	private synchronized String refreshToken() {
		if (tokenExpirationDate != null && new Date().before(tokenExpirationDate)) {
			return tokenCache.get("access_token");
		} else {
			try {
				TokenInfo tokenInfo = getServerToken(botConfig.getPrivateKey(), botConfig.getClientId(), botConfig.getClientSecret(), botConfig.getServiceAccount(), botConfig.getScope());
				String accessToken = tokenInfo.getAccessToken();
				tokenCache.put("access_token", accessToken);

				Duration expiresInDuration = Duration.ofSeconds(tokenInfo.getExpiresIn());
				// 현재 시간에서 expiresIn 기간을 더하고 1분(60s)을 뺀 시간 계산
				Instant expirationInstant = Instant.now().plus(expiresInDuration).minus(gracePeriod);
				tokenExpirationDate = Date.from(expirationInstant);

				return accessToken;
			} catch (IOException | GeneralSecurityException e) {
				throw new RuntimeException("Error refreshing token", e);
			}
		}
	}
	
	/**
	 * v2 서비스계정을 통한 토큰 발급
	 * @param serverPrivateKey
	 * @param clientId
	 * @param clientSecret
	 * @param serviceAccount
	 * @param scope
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	@SuppressWarnings({ "unchecked" })
	public TokenInfo getServerToken(String serverPrivateKey, String clientId, String clientSecret, String serviceAccount, String scope) throws IOException, GeneralSecurityException {
		
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("alg", "RS256");
		headers.put("typ", "JWT");
		Date issueDate = new Date();
		Date expireDate = DateUtils.addMinutes(new Date(), 30);
		String grantType = URLEncoder.encode("urn:ietf:params:oauth:grant-type:jwt-bearer", "UTF-8");
		
		//RSA
		RSAPublicKey publicKey = null;//Get the key instance
		RSAPrivateKey privateKey = getPrivateKeyFromString(serverPrivateKey);
		Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
		
		String assertion = JWT.create()
								.withHeader(headers)
								.withIssuer(clientId)
								.withSubject(serviceAccount)
								.withIssuedAt(issueDate)
								.withExpiresAt(expireDate)
								.sign(algorithm);
		
		// Parameter
		List<NameValuePair> params = new ArrayList<NameValuePair>(); 
		params.add(new BasicNameValuePair("assertion", assertion));
		params.add(new BasicNameValuePair("grant_type", grantType));
		params.add(new BasicNameValuePair("client_id", clientId));
		params.add(new BasicNameValuePair("client_secret", clientSecret));
		params.add(new BasicNameValuePair("scope", scope));

		TokenInfo tokenInfo = worksClient.postByFormUrlencoded("https://auth.worksmobile.com/oauth2/v2.0/token", params)
				.flatMap(content -> {
					try {
						return Mono.just(new ObjectMapper().readValue(content, TokenInfo.class));
					} catch (Exception e) {
						log.error("Error processing response", e);
						return Mono.error(e);
					}
				}).block();

		/*
			{
				"access_token": "kr1AAAAwQSFbOgcXEy7kRGlljKS5/8UwpRh454bljHQajmS7TK069czqA0JcuCcbfDNWRqouQVL/cw64btBW08PQALp10jr3cqgQrA9sdytxKo0+xVT90b3yHs+/6/PM//qEjubrjyYMO+Nt3lPZrFOzzJiRiAQqU0lor0zWk+ZNxMm6D40nB8jD74voYpLTKX+HjSh63Xihmq1ckyN72OjkmmRuZ5+9Qp5GPvWp8jnL8n2ewFI/3D8hg9KFicOUh5V6aKqaxDj+zYuA9xAPOTgJMRpNZA=",
				"refresh_token": "AAAAUrG721oexirJYyMXOdFMhVpRIDLP9g8gIIGf5xklkE+5FTIITjwlUGCyfJ5F3u4fWi4bIBheJ/2xrQ40M9VTd//g4aEqH1vBwjS6kKpGnUUen2oJqyNcel2fOz8E3nFKAQ==",
				"scope": "bot",
				"expire_in": "604800",
				"token_type": "Bearer"
			}
		*/

		return tokenInfo;
	}

	/**
	 * Private Key 값 파싱
	 * @param key
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	private RSAPrivateKey getPrivateKeyFromString(String key) throws IOException, GeneralSecurityException {
	    String privateKeyPEM = key;
	    privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "");
	    privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
	    byte[] encodedPrivateKeyPEM = Base64.decodeBase64(privateKeyPEM);
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedPrivateKeyPEM);
	    RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	    
	    return privateKey;
	}
}
