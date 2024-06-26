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
	public class MessageEvent {

		@JsonProperty("type")
		private String type;

		@JsonProperty("source")
		private Source source;

		@JsonProperty("issuedTime")
		private String issuedTime;

		@JsonProperty("content")
		private Content content;

		@Data
		@NoArgsConstructor
		@AllArgsConstructor
		@Builder
		public static class Source {

			@JsonProperty("userId")
			private String userId;

			@JsonProperty("channelId")
			private String channelId;

			@JsonProperty("domainId")
			private long domainId;
		}

		@Data
		@NoArgsConstructor
		@AllArgsConstructor
		@Builder
		public static class Content {

			@JsonProperty("type")
			private String type;

			@JsonProperty("text")
			private String text;
		}

		private String additionalText;
	}