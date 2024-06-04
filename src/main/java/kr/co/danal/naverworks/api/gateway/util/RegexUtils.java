package kr.co.danal.naverworks.api.gateway.util;

import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class RegexUtils {

	/**
	 * jsonStr 문자열 안에 숫자로 되어 있는 항목 문자열로 변경 (javascript 에서 너무 큰 숫자 JSON.parse 에서 엄한값으로 변경처리를 해버리고 있어서 여기서 수정해서 전달)
	 * @param jsonStr
	 * @return
	 */
	public static String convertJsonNumberToJsonString(String jsonStr) {
		
		Pattern pattern = Pattern.compile(":[0-9]+[,]");
		Matcher matcher = pattern.matcher(jsonStr);
		
		while( matcher.find() ) {
			//: , 사이 값에 " 를 추가해주어야 하기때문에 범위 조절
			String matcherGroup = matcher.group();
			matcherGroup = matcherGroup.replaceFirst(":", ":\"");
			matcherGroup = matcherGroup.replaceFirst(",", "\",");
			jsonStr = jsonStr.replaceFirst(matcher.group(), matcherGroup);
			
			log.info("### response regexStr :: {} => {} = {} ", matcher.group(), matcherGroup, jsonStr);
			
		}
		
	    return jsonStr;
	}
	
}
