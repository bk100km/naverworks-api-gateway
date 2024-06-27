package kr.co.danal.naverworks.api.gateway.service;

import kr.co.danal.naverworks.api.gateway.model.TokenInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JWTServiceTest {

	@Autowired
	JWTService jwtService;

//	@Test
	public void getTokenAuto() {
		String token = jwtService.getServerToken();
		assertThat(token).isNotNull();
	}

//	@Test
	public void getTokenCustom() {
		String serverPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
				"MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCrdJNJKh5AGV9M\n" +
				"Jr1xiKKxaenG/ARymXxFdpWi+Mz0pGJsat3gdJeE07m4S3zWbZ0m2E5CifJ8mZwU\n" +
				"6KsPzcKtqxMDn6T66119S9fWXTBy/ypTsjSbyhecpk9T1T55YZ7WwyKUjAw3jQFJ\n" +
				"fP4i0yQEWvRR9bs3aieHhhTbl/FhWSKDAAfZKvL3BmybO9rsObUSqWlN2q86o8YR\n" +
				"oDVZVQvHf/83pBzyLP2INRG8XQScFGnAsHnu2l2hQR1u5SJS92s5ZnJDR+UmyxhT\n" +
				"l8aLPxx5wDp/hDs3YhGo89/EWL7xd1FZieRx8NXPezReIloRprJIbNOUvE0OE+mC\n" +
				"ktHAtJD7AgMBAAECggEAFztEebVrQ+QsNXVKVgfdg4yGUJ/1OlHxtLfMrrhnjP55\n" +
				"F5ejWLZzkVve3kncMHHJyWmlsP5gDOIDdBHSm+GVb1Ku0N7gy974W/Ha3QDjf7Fz\n" +
				"WsPavQE1EdS3CvTp8ih4DUGDff076osAkaSOCsnxJ2XrlO9m3qtTj9lrGhYMbtQb\n" +
				"w1ycfzYVmJviitaSuf0bsFluXce3MWCkG3udM11XkT3A0vybZvzOL40JDo4MYj35\n" +
				"nQHdzJBakaLMqHs/NReK3WuNUXBgKVw/FSaTqBVCczXFeBqvF7cVA5PILRmlgwt8\n" +
				"4xtPRYsAdpAgvoVk3MpfwWQk+Qu5enHv7nYjFRpZAQKBgQDvHj1tUzSMn51uOtLN\n" +
				"uKYDxHfDK/e9aii7RtJg42HCqWsuMYuslUoGrUHWDfvxuQpML8H31kPAnX7WpYPR\n" +
				"zQvhYw7IPz9ya69ujhT20OrVg29LJHWL+fWOPvK/X8pVjyayuUZ6RqQB0pZqdq/S\n" +
				"UheOPnMr6NzS/uCEfa7ODpC6GQKBgQC3j2qBvFEhMJ8Z28P9gJzBD8asYdOUYKEr\n" +
				"MTZJMHULz4N1rDk8Iv4HkNMob3FdJD1vWqIWqtyE+MO1oBmY7m6JsbfCFozItMED\n" +
				"N0eEHqCK/BNWRzf9lc7bNSrDMU3CgLq2MNzyN3U+9JLvTiG2Rc7k99+kD/JQ1Eqe\n" +
				"qk77h8cuMwKBgQCcHq1ShMHJkqkyABwEjyttSEg9WKaeuAIyDBJtiegZNY4yEc00\n" +
				"+B/l7hcPEABQGVVc2s2U6ANCblITCOvg/jRkis1q9siRBzf6Qa/0WgEJOJBgikRp\n" +
				"COFHZ1gMlRlA9X75r7gRO6/3NKxahHVVJ54TlDiUEayj4h9qwbhw9teNeQKBgFY2\n" +
				"oOKQTVaWXJOrmZDuIDG5NCnwxIwzGl+L/KBBhyfl7iuNmVA6ay+ogNTECu2hQM2V\n" +
				"Nv4IgAxgPqOGr58ZlYdMli8toi/+XY0iII/YRrUgkgB68LxE6WQhw5TsCkapaYh6\n" +
				"yLtm+sV3369P9huDDBqUpVCMQNZf7zA4c2JGMnHrAoGAKU9VfnS+wyyH8xOu1XG1\n" +
				"C0c19H3DNQa/SopiT4Dr+dXcVpLGDg/4RKh1hFbfsXaH7WiPSr+1AQW+syyXT/sH\n" +
				"pa0s4bD3IdogBj4r2yoAdvgSebE5+9EBl46s0S2TY8Gvc6+/ExQBEPUvid4XW57z\n" +
				"GeWpOx+foGRV/csMvxNlMwg=\n" +
				"-----END PRIVATE KEY-----\n";



		String clientId = "vcLxZndZyYDwtBuTvxuC";
		String clientSecret = "MXmguprU1i";
		String serviceAccount = "a7vcu.serviceaccount@danal3.by-works.com";
		String scope = "audit audit.read board board.read bot bot.read calendar calendar.read contact contact.read directory directory.read group group.read orgunit orgunit.read user user.read";

		TokenInfo tokenMap = jwtService.getServerToken(serverPrivateKey, clientId, clientSecret, serviceAccount, scope);
		assertThat(tokenMap).isNotNull();
	}

}
