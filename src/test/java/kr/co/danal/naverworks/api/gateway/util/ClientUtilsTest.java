package kr.co.danal.naverworks.api.gateway.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class ClientUtilsTest {

	@Autowired
    ClientUtils utils;

	String accessToken = "kr1AAABguvOIhj8no/qrYZit8R0kMXT99uOWhksM7gJZvApje5r0QRGVAIaru62s7zYYkVcyTZxJe2uSRtjVGCGUJcuHD0nfoqBDM7f6jfEoM95rShhyGuzm0qV4X6wkFGOqGp6j0VZO2imOwpLRM4Xe6vI51BZCQpEZobENYE+MVOqn26cPwWXwmEJdRYPz3FnLeU3okJV4QzwH3ShOfMANW/HJcg8b9A2dbDvCSRi/+ZndoDdGb7B8JH/owLGD4QWKT3DJsjM3mXup9tLwkp3YzhJ9RnqOO00CLa3bI4fFKzi+3tJ/zJmDV4lt3DE7t5LZrzqoNUJwkoQpowDLeYonzcRT5EA3deknCHO0NPkiCGSEcBmSd/i72RkgIhUAHxdhnWADuy7TudSYqaw8VVm5lGydp6D8cCKt0ZPW+4p3DZ0L5zfbIA9GGaupDv/5mw83EOMdhKS02vINZPXEbAvwbKk1s6G8qxtmLZV+581joTCnECWsLDh+3kQj29KFxr5lpIvg0L0RSb5+uv/nt1nkllDQ8k=.kwiu9yNovfcs8Rumz2QSOg";
	
//	@Test
	public void get() throws GeneralSecurityException, IOException {
		utils.get("https://www.worksapis.com/v1.0/directory/levels");
	}
	
//	@Test
	public void add() throws GeneralSecurityException, IOException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("domainId", "300013365");
		params.put("displayOrder", "9999");
		params.put("levelName", "샘플직급3");
		params.put("levelExternalKey", "bbb");
		params.put("executive", false);
		
		utils.post("https://www.worksapis.com/v1.0/directory/levels", params);
	}
}
