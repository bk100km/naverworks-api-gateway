package kr.co.danal.naverworks.api.gateway.util;

import kr.co.danal.naverworks.api.gateway.model.ResponseData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Component
public class ClientUtils {
	
	/**
	 * get 방식으로 호출 
	 * @param accessToken
	 * @param path
	 * @param params
	 * @return
	 */
	public InputStream getByUrl( String accessToken, String url ) {
		
		try {
			
			// 호출 URL 셋팅
			URI uri = new URI(url);
			URIBuilder ub = new URIBuilder(uri);
			
			// 컨텐츠 타입
			String contentType = "application/x-www-form-urlencoded";
			
			//전송 준비 
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			//호출 하기 위해 준비 
			HttpGet http = new HttpGet(ub.toString());
			http.addHeader("Authorization", "Bearer " + accessToken);
			http.addHeader("Content-Type", contentType);
			log.info("### request => {}, header[Content-Type={}] , content={} ", http.toString(), contentType);
			
			//호출 
			HttpResponse response;
			response = httpClient.execute(http);
			log.info("### response :: {} ", response);
			
			//200이면 성공
			log.info("### response StatusCode :: {} ", response.getStatusLine().getStatusCode());
			
			//200이 아닐 경우 리턴 데이타 받아옴 
			HttpEntity entity = response.getEntity();
			
			log.info( "contenttype={} {}, contentlength={}", entity.getContentType().getName(), entity.getContentType().getValue(), entity.getContentLength() );
			
			if( entity.getContentType().getValue().equalsIgnoreCase("application/json;charset=UTF-8") ) log.info("{}", entity.getContent().toString());
			
			return entity.getContent();
			
		} catch (Exception e) {
			log.error("Exception :: {}",  e.getMessage(), e);
			return null;
		}
	}
	
	
	/**
	 * get 방식으로 호출
	 * @param accessToken 
	 * @param uri
	 * @return
	 */
	public ResponseData get(String accessToken, String uri ) {
		return this.get(accessToken, uri, null);
	}
	
	/**
	 * get 방식으로 호출
	 * @param accessToken
	 * @param uri
	 * @param params
	 * @return
	 */
	public ResponseData get( String accessToken, String uri, List<NameValuePair> params ) {
		
		try {
			
			// 호출 URL 셋팅
			URIBuilder ub = new URIBuilder(uri);
			if(params != null) ub.addParameters(params);  
			
			// 컨텐츠 타입
			String contentType = "application/x-www-form-urlencoded";
			
			//전송 준비 
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			//호출 하기 위해 준비 
			HttpGet http = new HttpGet(ub.toString());
			http.addHeader("Authorization", "Bearer " + accessToken);
			http.addHeader("Content-Type", contentType);
			log.info("### request => {}, header[Content-Type={}] , content={} ", http.toString(), new ObjectMapper().writeValueAsString(http.getAllHeaders()), contentType);
			
			//호출 
			HttpResponse response = httpClient.execute(http);
			log.info("### response :: {} ", response);
			
			//200이면 성공
			log.info("### response StatusCode :: {} ", response.getStatusLine().getStatusCode());
			
			//200이 아닐 경우 리턴 데이타 받아옴 
			HttpEntity entity = response.getEntity();
			
			log.info("### response content type :: {} , {}", entity.getContentType().getName(), entity.getContentType().getValue());
			
			ResponseData responseData;
			String[] contentTypes = {"application/json", "text/html"};    
			if( Arrays.asList(contentTypes).contains(entity.getContentType().getValue().toLowerCase()) ) { //리턴 형태가 파일이 아닌 경우
				String responseStr = EntityUtils.toString(entity);
				log.info("### response responseStr :: {}", responseStr);
				
				if( StringUtils.isEmpty(responseStr) ) responseStr = "{}";
				
				//게시판아이디(boardId) 숫자 범위가 너무 커서 자바스크립트 JSON.parse 에서 잘못 파싱이 되고 있다보니 숫자->문자형으로 치환 해서 리턴 하도록 로직 추가
				if( uri.indexOf("boards") > -1 ) responseStr = RegexUtils.convertJsonNumberToJsonString(responseStr);
				
				responseData = ResponseData.builder().status(response.getStatusLine().getStatusCode()).message("").data(new ObjectMapper().reader().readValue(responseStr, Object.class)).build();
					
				
			} else { //파일 일 때
				responseData = ResponseData.builder().status(response.getStatusLine().getStatusCode()).message("file").data(entity.getContent()).build();
				
			}
			
			return responseData;
			
		} catch (Exception e) {
			log.error("Exception :: {}",  e.getMessage(), e);
			return ResponseData.builder().status(500).message(e.getMessage()).data(e).build();
		} 
		
	}
	
	/**
	 * post 형태로 데이타 전송
	 * @param accessToken
	 * @param uri
	 * @param requestData
	 * @return
	 */
	public ResponseData post( String accessToken, String uri, Object data ) {
		
		try {
			
			// 호출 URL 셋팅
			URIBuilder ub = new URIBuilder(uri);
			
			//전송 준비 
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			//호출 하기 위해 준비 
			HttpPost http = new HttpPost(ub.toString());
			http.addHeader("Authorization", "Bearer " + accessToken);
			
			// File Type일 경우
			// Object로 받을 꺼고 추가 파라미터 넣기 귀찮아서 예외처리 함
			try {
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				builder.addPart("upfile", new FileBody((File) data, ContentType.DEFAULT_BINARY));
				http.setEntity(builder.build());
				log.info("### request => {}, header[Content-Type={}, Authorization={}] ", http.toString(), "Bearer " + accessToken);
				
			}catch(Exception e) { //File Type이 아닐 경우
				String contentType = ContentType.APPLICATION_JSON.toString();
				http.addHeader("Content-Type", contentType);
				http.addHeader("Accept", contentType);
				if( data != null ) http.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(data), "UTF-8"));
				log.info("### request => {}, header[Content-Type={}, Authorization={}] , content={} ", http.toString(), contentType, "Bearer " + accessToken, data);
				
			}
			
			//호출 
			HttpResponse response = httpClient.execute(http);
			log.info("### response :: {} ", response);
			
			//200이면 성공
			log.info("### response StatusCode :: {} ", response.getStatusLine().getStatusCode());
			
			//200이 아닐 경우 리턴 데이타 받아옴 
			HttpEntity entity = response.getEntity();
			String responseStr = EntityUtils.toString(entity);
			log.info("### response responseStr :: {}", responseStr);
			
			if( StringUtils.isEmpty(responseStr) ) responseStr = "{}";
			
			//게시판아이디(boardId) 숫자 범위가 너무 커서 자바스크립트 JSON.parse 에서 잘못 파싱이 되고 있다보니 숫자->문자형으로 치환 해서 리턴 하도록 로직 추가
			if( uri.indexOf("boards") > -1 ) responseStr = RegexUtils.convertJsonNumberToJsonString(responseStr);
			
			return ResponseData.builder().status(response.getStatusLine().getStatusCode()).message("").data(new ObjectMapper().reader().readValue(responseStr, Object.class)).build();
			
		} catch (Exception e) {
			log.error("Exception :: {}",  e.getMessage(), e);
			return ResponseData.builder().status(500).message(e.getMessage()).data(e).build();
		} 
		
	}
	
	
	/**
	 * patch 형태로 데이타 전송
	 * @param accessToken
	 * @param uri
	 * @param requestData
	 * @return
	 */
	public ResponseData patch( String accessToken, String uri, Object data ) {
		
		try {
			
			// 호출 URL 셋팅
			URIBuilder ub = new URIBuilder(uri);
			
			// 컨텐츠 타입
			String contentType = "application/json";
			
			//전송 준비 
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			//호출 하기 위해 준비 
			HttpPatch http = new HttpPatch(ub.toString());
			http.addHeader("Authorization", "Bearer " + accessToken);
			http.addHeader("Accept", contentType);
			http.addHeader("Content-Type", contentType);
			if( data != null ) http.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(data), "UTF-8"));
			log.info("### request => {}, header[Content-Type={}] , content={} ", http.toString(), contentType, data);
			
			//호출 
			HttpResponse response = httpClient.execute(http);
			log.info("### response :: {} ", response);
			
			//200이면 성공
			log.info("### response StatusCode :: {} ", response.getStatusLine().getStatusCode());
			
			//200이 아닐 경우 리턴 데이타 받아옴 
			HttpEntity entity = response.getEntity();
			String responseStr = EntityUtils.toString(entity);
			log.info("### response responseStr :: {}", responseStr);
			
			if( StringUtils.isEmpty(responseStr) ) responseStr = "{}";
			
			//게시판아이디(boardId) 숫자 범위가 너무 커서 자바스크립트 JSON.parse 에서 잘못 파싱이 되고 있다보니 숫자->문자형으로 치환 해서 리턴 하도록 로직 추가
			if( uri.indexOf("boards") > -1 ) responseStr = RegexUtils.convertJsonNumberToJsonString(responseStr);
			
			return ResponseData.builder().status(response.getStatusLine().getStatusCode()).message("").data(new ObjectMapper().reader().readValue(responseStr, Object.class)).build();
			
		} catch (Exception e) {
			log.error("Exception :: {}",  e.getMessage(), e);
			return ResponseData.builder().status(500).message(e.getMessage()).data(e).build();
		} 
		
	}
	
	
	/**
	 * put 형태로 데이타 전송
	 * @param accessToken
	 * @param uri
	 * @param requestData
	 * @return
	 */
	public ResponseData put( String accessToken, String uri, Object data ) {
		
		try {
			
			// 호출 URL 셋팅
			URIBuilder ub = new URIBuilder(uri);
			
			// 컨텐츠 타입
			String contentType = "application/json";
			
			//전송 준비 
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			//호출 하기 위해 준비 
			HttpPut http = new HttpPut(ub.toString());
			http.addHeader("Authorization", "Bearer " + accessToken);
			http.addHeader("Accept", contentType);
			http.addHeader("Content-Type", contentType);
			if( data != null ) http.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(data), "UTF-8"));
			log.info("### request => {}, header[Content-Type={}] , content={} ", http.toString(), contentType, data);
			
			//호출 
			HttpResponse response = httpClient.execute(http);
			log.info("### response :: {} ", response);
			
			//200이면 성공
			log.info("### response StatusCode :: {} ", response.getStatusLine().getStatusCode());
			
			//200이 아닐 경우 리턴 데이타 받아옴 
			HttpEntity entity = response.getEntity();
			String responseStr = EntityUtils.toString(entity);
			log.info("### response responseStr :: {}", responseStr);
			
			if( StringUtils.isEmpty(responseStr) ) responseStr = "{}";
			
			//게시판아이디(boardId) 숫자 범위가 너무 커서 자바스크립트 JSON.parse 에서 잘못 파싱이 되고 있다보니 숫자->문자형으로 치환 해서 리턴 하도록 로직 추가
			if( uri.indexOf("boards") > -1 ) responseStr = RegexUtils.convertJsonNumberToJsonString(responseStr);
			
			return ResponseData.builder().status(response.getStatusLine().getStatusCode()).message("").data(new ObjectMapper().reader().readValue(responseStr, Object.class)).build();
			
		} catch (Exception e) {
			log.error("Exception :: {}",  e.getMessage(), e);
			return ResponseData.builder().status(500).message(e.getMessage()).data(e).build();
		} 
		
	}
	
	
	/**
	 * delete 방식으로 호출 
	 * @param accessToken
	 * @param path
	 * @return
	 */
	public ResponseData delete( String accessToken, String path ) {
		return this.delete(accessToken, path, null);
	}
	
	/**
	 * delete 방식으로 호출 
	 * @param uri
	 * @param params
	 * @return
	 */
	public ResponseData delete( String accessToken, String uri, List<NameValuePair> params ) {
		
		try {
			
			// 호출 URL 셋팅
			URIBuilder ub = new URIBuilder(uri);
			if(params != null) ub.addParameters(params);
			
			// 컨텐츠 타입
			String contentType = "application/x-www-form-urlencoded";
			
			//전송 준비 
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			//호출 하기 위해 준비 
			HttpDelete http = new HttpDelete(ub.toString());
			http.addHeader("Authorization", "Bearer " + accessToken);
			http.addHeader("Content-Type", contentType);
			log.info("### request => {}, header[Content-Type={}] , content={} ", http.toString(), contentType);
			
			//호출 
			HttpResponse response;
			response = httpClient.execute(http);
			log.info("### response :: {} ", response);
			
			//200이면 성공
			int statusCode = response.getStatusLine().getStatusCode();
			log.info("### response StatusCode :: {} ", statusCode);
			
			
			if( statusCode == 204 ) return ResponseData.builder().status(response.getStatusLine().getStatusCode()).message("").data(new ObjectMapper().reader().readValue("{}", Object.class)).build();
			
			
			//200이 아닐 경우 리턴 데이타 받아옴 
			HttpEntity entity = response.getEntity();
			
			String responseStr = EntityUtils.toString(entity);
			log.info("### response responseStr :: {}", responseStr);
			
			if( StringUtils.isEmpty(responseStr) ) responseStr = "{}";
			
			//게시판아이디(boardId) 숫자 범위가 너무 커서 자바스크립트 JSON.parse 에서 잘못 파싱이 되고 있다보니 숫자->문자형으로 치환 해서 리턴 하도록 로직 추가
			if( uri.indexOf("boards") > -1 ) responseStr = RegexUtils.convertJsonNumberToJsonString(responseStr);
			
			return ResponseData.builder().status(response.getStatusLine().getStatusCode()).message("").data(new ObjectMapper().reader().readValue(responseStr, Object.class)).build();
			
		} catch (Exception e) {
			log.error("Exception :: {}",  e.getMessage(), e);
			return ResponseData.builder().status(500).message(e.getMessage()).data(e).build();
		} 
		
	}
	
	/**
	 * Post 전송 - FormUrlencoded 형
	 * @param params
	 * @return
	 */
	public String getByFormUrlencoded(String targetUrl, List<NameValuePair> headers) {

		String content = "";;

		try {
			//전송 준비Ø
			HttpClient httpClient = HttpClientBuilder.create().build();

			//get로 호출 하기 위해 준비
			HttpGet http = new HttpGet(targetUrl);
			http.addHeader("Content-Type", "application/x-www-form-urlencoded");
			headers.stream().forEach(o->{
				http.addHeader(o.getName(), o.getValue());
			});
			log.info("### Request={}", http.toString());

			//호출
			HttpResponse response = httpClient.execute(http);
			log.info("### Response={}", response.toString());

			//리턴 데이타 받아옴
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity);

		} catch (ParseException | IOException e) {
			log.error("### Exception={}", e.getMessage(), e);

		} finally {
			log.info("### Content={}", content);

		}

		return content;

	}

	/**
	 * Post 전송 - FormUrlencoded 형, accesstoken 발급 받을 때 사용함
	 * @param params
	 * @return
	 */
	public String postByFormUrlencoded(String targetUrl, List<NameValuePair> params) {

		String content = "";;

		try {
			//전송 준비Ø
			HttpClient httpClient = HttpClientBuilder.create().build();

			//post로 호출 하기 위해 준
			HttpPost http = new HttpPost(targetUrl);
			http.addHeader("Content-Type", "application/x-www-form-urlencoded");
			http.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			log.info("### Request={}, params={}", http.toString(), params);

			//호출
			HttpResponse response = httpClient.execute(http);
			log.info("### Response={}", response.toString());

			//리턴 데이타 받아옴
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity);

		} catch (ParseException | IOException e) {
			log.error("### Exception={}", e.getMessage(), e);

		} finally {
			log.info("### Content={}", content);

		}

		return content;

	}
	
}
