package eu.miroslavlehotsky.sporttracker.model;

import lombok.Data;

@Data
public class OutdoorPlayground {
	private long id;
	private String name;
	private String surface;
	private Polygon polygon;

	@Override
	public String toString() {
		String result = "{\"type\": \"Feature\", \"geometry\":" + polygon + ", \"properties\": {";
		result += name != null ? "\"title\": \"" + name + "\"," : "";
		result += surface != null ? "\"surface\": \"" + surface + "\"," : "";
		result += "\"fill\": \"#D8003A\",";
		result += "\"stroke\": \"#D8003A\",";
		result += "\"stroke-width\": 1";
		result += "}}";

		return result;
	}
}
