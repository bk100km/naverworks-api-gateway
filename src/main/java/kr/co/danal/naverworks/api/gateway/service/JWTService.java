package kr.co.danal.naverworks.api.gateway.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import kr.co.danal.naverworks.api.gateway.config.BotConfig;
import kr.co.danal.naverworks.api.gateway.util.ClientUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JWTService {

	private final ClientUtils worksClient;
	private final BotConfig botConfig;
	
	public String getServerToken() throws IOException, GeneralSecurityException {

		log.info("[getServerToken] privatekey - {}\n clientid - {}\n clientsecret - {}\n serviceaccount - {}\n scope - {}", botConfig.getPrivateKey(), botConfig.getClientId(), botConfig.getClientSecret(), botConfig.getServiceAccount(), botConfig.getScope());
		Map<String, Object> resultMap = getServerToken(botConfig.getPrivateKey(), botConfig.getClientId(), botConfig.getClientSecret(), botConfig.getServiceAccount(), botConfig.getScope());
		String accessToken = (String) resultMap.get("access_token");
	  	log.info("access token - {}", accessToken);
		
	  	return accessToken;
		
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
	public Map<String, Object> getServerToken(String serverPrivateKey, String clientId, String clientSecret, String serviceAccount, String scope) throws IOException, GeneralSecurityException {
		
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("alg", "RS256");
		headers.put("typ", "JWT");
		Date iat = new Date();
		Date exp = DateUtils.addMinutes(new Date(), 30);
		String grantType = URLEncoder.encode("urn:ietf:params:oauth:grant-type:jwt-bearer", "UTF-8");
		
		//RSA
		RSAPublicKey publicKey = null;//Get the key instance
		RSAPrivateKey privateKey = getPrivateKeyFromString(serverPrivateKey);
		Algorithm algorithmRS = Algorithm.RSA256(publicKey, privateKey);
		
		String assertion = JWT.create()
								.withHeader(headers)
								.withIssuer(clientId)
								.withSubject(serviceAccount)
								.withIssuedAt(iat)
								.withExpiresAt(exp)
								.sign(algorithmRS);
		
		// Parameter
		List<NameValuePair> params = new ArrayList<NameValuePair>(); 
		params.add(new BasicNameValuePair("assertion", assertion));
		params.add(new BasicNameValuePair("grant_type", grantType));
		params.add(new BasicNameValuePair("client_id", clientId));
		params.add(new BasicNameValuePair("client_secret", clientSecret));
		params.add(new BasicNameValuePair("scope", scope));
		
/*
	    {
	 	    "access_token": "kr1AAAAwQSFbOgcXEy7kRGlljKS5/8UwpRh454bljHQajmS7TK069czqA0JcuCcbfDNWRqouQVL/cw64btBW08PQALp10jr3cqgQrA9sdytxKo0+xVT90b3yHs+/6/PM//qEjubrjyYMO+Nt3lPZrFOzzJiRiAQqU0lor0zWk+ZNxMm6D40nB8jD74voYpLTKX+HjSh63Xihmq1ckyN72OjkmmRuZ5+9Qp5GPvWp8jnL8n2ewFI/3D8hg9KFicOUh5V6aKqaxDj+zYuA9xAPOTgJMRpNZA=",
	 	    "refresh_token": "AAAAUrG721oexirJYyMXOdFMhVpRIDLP9g8gIIGf5xklkE+5FTIITjwlUGCyfJ5F3u4fWi4bIBheJ/2xrQ40M9VTd//g4aEqH1vBwjS6kKpGnUUen2oJqyNcel2fOz8E3nFKAQ==",
	 	    "scope": "bot",
	 	    "expire_in": "604800",
	 	    "token_type": "Bearer"
	 	}
*/
		String content = worksClient.postByFormUrlencoded("https://auth.worksmobile.com/oauth2/v2.0/token", params);
		Map<String, Object> resultMap = new ObjectMapper().readValue(content, HashMap.class);
		
		return resultMap;
		
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
	    byte[] encoded = Base64.decodeBase64(privateKeyPEM);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
	    RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
	    
	    return privKey;
	}
	
}
