package eu.miroslavlehotsky.sporttracker.model;

import lombok.Data;

@Data
public class CycleRoad {
	private long id;
	private String name;
	private LineString lineString;

	@Override
	public String toString() {
		String result = "{\"type\": \"Feature\", \"geometry\":" + lineString + ", \"properties\": {";
		result += name != null ? "\"title\": \"" + name + "\"," : "";
		result += "\"stroke\": \"#D8003A\",";
		result += "\"stroke-width\": 3";
		result += "}}";

		return result;
	}
}
