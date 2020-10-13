package eu.miroslavlehotsky.sporttracker.model;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Position {
	private double lat;
	private double lon;

	@Override
	public String toString() {
		return Arrays.asList(lat, lon).toString();
	}

	public String reverseCoordinatesToString() {
		return Arrays.asList(lon, lat).toString();
	}
}
