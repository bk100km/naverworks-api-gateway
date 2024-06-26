package kr.co.danal.naverworks.api.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Position {
	private String positionId;
	private long domainId;
	private int displayOrder;
	private String positionName;
	private String positionExternalKey;
	private List<Object> i18nNames; // Assuming i18nNames is a list of objects

	@Override
	public String toString() {
		return "Position{" +
				"positionId='" + positionId + '\'' +
				", domainId=" + domainId +
				", displayOrder=" + displayOrder +
				", positionName='" + positionName + '\'' +
				", positionExternalKey='" + positionExternalKey + '\'' +
				", i18nNames=" + i18nNames +
				'}';
	}
}
