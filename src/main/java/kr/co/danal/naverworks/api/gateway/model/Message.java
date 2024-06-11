package kr.co.danal.naverworks.api.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

	@JsonProperty("content")
	private Content content;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Content {
		@JsonProperty("type")
		private String type;
		@JsonProperty("text")
		private String text;

		@Override
		public String toString() {
			return "content {" +
					"type='" + type + '\'' +
					", text='" + text + '\'' +
					'}';
		}
	}

	@Override
	public String toString() {
		return "Message {" +
				content + '\'' +
				'}';
	}
}
