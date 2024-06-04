package kr.co.danal.naverworks.api.gateway.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@SpringBootTest
public class JWTServiceTest {

	@Autowired
	JWTService jwtService;

	@Test
	public void getTokenAuto() throws IOException, GeneralSecurityException {
		String token = jwtService.getServerToken();
		assertThat(token).isNotNull();
	}

	@Test
	public void getTokenCustom() throws IOException, GeneralSecurityException {
		String serverPrivateKey = "-----BEGIN PRIVATE KEY-----" +
				"MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDQOfAdsmBCQbEK" +
				"VOwOjHSLzw7f7BT1iMNjIk7FPHSxCgOvfIZC9f0rkPk9H6EchgPkFrwGuSDOkdnw" +
				"//ARjgGg9gyo/2b3g6wS5ilYb67VDLuP8eXH4jDIKEs+NpQZM7JHarwNhVbHdUuX" +
				"6nd1CzWePp/wDC9EMJWjVpFBuiAKlStgYkG5pCA0IP6VtayrcCZ7aIuK5fRRXqrW" +
				"fEBYwfx+M9D/miyLyOcfqM4gW0cI+vsNCzgoEXfp+zbHPtutNqhdC332kSHNctRM" +
				"9besBkOFTakWrWxQSBd/2cGhxFrf90zMmrHPBqkFjA2ubNrtCYDRoFC3SkWOLz9z" +
				"3KMiCGFtAgMBAAECggEAUk5khEiK1gXDZjMVL9cDBnRtC8P0sO1DVj8sZbg04/ny" +
				"ZP5Sz8sOLJdcfXWwKGcsyI7X+cxZlG2AeUuhaD76k9ZOQMrbt4CblsjVlPhoYhxr" +
				"U0Wpry3QXH3Y6BmIxsFxedtNxAZtu9+4Zq8uUtyAcDGO5MLcOBcAPxUry5A3tBZ2" +
				"DX6ZLRmehMQKTFORHVV+XhXwBiR2hOuOACXxgkU3lpcaLaohxsSfTILPTgmJjOaO" +
				"mPIjKhN3LIGYEXP01mhI8tM+ttzXEFov9iuQETv7/1mj8YDds6veQLy31oc8I9fg" +
				"y9mFs3ZMV/WJZtKM4OFYaQpjKq7fG5ha/rkBbbCA3QKBgQDnSAa//r8O1q+QQz/M" +
				"NlF9smC6hy4Ce1AHqe7j3/MTRtlxawISNBsWGxYTavXofFZ8afiDpp8G9S4d7HT1" +
				"jntDfp1ZtHpb9MWVitgUBSXTD3Iqx+/EilnyyWe+5fje5hKfirltsOmFYe2zbSsc" +
				"HbfHZoGplnQm57fgAnihuIyizwKBgQDmex1FgrJG2piuwr56N3LMwsgsccXA58Ft" +
				"leC9wrrzQi1aILOpAPpam3bfgHLInIMtONJUuYNErJLxteXfapE5D0lGhNaRiaVT" +
				"jAjsd2RV50eaOTqiYcwrP648xQi30oWsGpM15a+lSrgK+dGD9OtFolBtgQy7LkD+" +
				"qkhnIhg3AwKBgGsQOp+yS7uQwmDj4KqmEn8/jeuLHE9lyhxiHAY864dm6s3IHYNT" +
				"v54yF7wRbZibxmV9oN9AyKkt4pVTmqj5tjH+cNTP6TycSML7LR/HakUUB2OqfmHb" +
				"MdxkQ7RpPRXiJ4jXbRNMLC3ksWa4CJbRYvvZhfh9f3NrHQGD41fp2KCdAoGBAJcB" +
				"LopuONOHQOWvHhEA5ywUqgXEfOYIcYiU2+w3+ziJsSENr+gYhe57UQTaKVoagS9N" +
				"vTBZYxHJZ3UJ0hPwxDj4NPAI9HPiFPmKFAapqolFwHng6POe05+6VzPle/LD8WRy" +
				"/OQhGlVXgQFf789e1g6Ha95y4J56jYbMlidMBUV1AoGAevaEE08LZ6PODqLss6dx" +
				"JN+VZE7cGXFhAI/5EdR0sN2P4wH9aWm9VGGAmqBgqPNaGTkGCDl+VZdNeeOCVODf" +
				"GRBQsblx7peQzwiegdn00foMlQg1ltOXgSwJeg7CRKs+a2qg4isjQwThH6x5LFhv" +
				"hpsTot4zNTugMvX4LRmCsO8=" +
				"-----END PRIVATE KEY-----";



		String clientId = "asf2CaB2lu";
		String clientSecret = "U23coC";
		String serviceAccount = "serviceaccount@mail.workslife.kr";
		String scope = "audit audit.read board board.read bot bot.read calendar calendar.read contact contact.read directory directory.read file file.read group group.folder group.folder.read group.read mail mail.read orgunit orgunit.read user user.read";

		Map<String, Object> token = jwtService.getServerToken(serverPrivateKey, clientId, clientSecret, serviceAccount, scope);
		assertThat(token).isNotNull();
	}

}
