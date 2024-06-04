package kr.co.danal.naverworks.api.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message {

	@JsonProperty("content")
	private Content content;

	@Data
	@NoArgsConstructor
	public static class Content {
		@JsonProperty("type")
		private String type;
		@JsonProperty("text")
		private String text;

		// toString 메서드 재정의
		@Override
		public String toString() {
			return "content {" +
					"type='" + type + '\'' +
					", text='" + text + '\'' +
					'}';
		}
	}

	// toString 메서드 재정의
	@Override
	public String toString() {
		return "Message {" +
				content + '\'' +
				'}';
	}
}
