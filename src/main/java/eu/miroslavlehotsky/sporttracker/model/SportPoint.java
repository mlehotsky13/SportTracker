package eu.miroslavlehotsky.sporttracker.model;

import java.util.Set;
import lombok.Data;

@Data
public class SportPoint {
	private long id;
	private String name;
	private String leisure;
	private Set<String> sports;
	private Point point;

	@Override
	public String toString() {
		String result = "{\"type\": \"Feature\", \"geometry\":" + point + ", \"properties\": {";
		result += name != null ? "\"title\": \"" + name + "\"," : "";
		result += leisure != null ? "\"leisure\": \"" + leisure + "\"," : "";
		result += sports != null ? "\"sports\": \"" + sports + "\"," : "";
		result += "\"marker-color\": \"#D8003A\", \"marker-size\": \"medium\", \"marker-symbol\": \"circle\"}}";

		return result;
	}
}
