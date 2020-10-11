package eu.miroslavlehotsky.sporttracker.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import eu.miroslavlehotsky.sporttracker.model.CycleRoad;
import eu.miroslavlehotsky.sporttracker.model.OutdoorPlayground;
import eu.miroslavlehotsky.sporttracker.model.Point;
import eu.miroslavlehotsky.sporttracker.model.Position;
import eu.miroslavlehotsky.sporttracker.model.SportPoint;
import eu.miroslavlehotsky.sporttracker.repository.PointRepository;

@Service
public class SportServiceImpl implements SportService {

	@Autowired
	private PointRepository pointRepository;

	@Override
	public List<SportPoint> getSportPointData() {
		return getSportPointData("", "", "");
	}

	@Override
	public List<SportPoint> getSportPointData(String streetName, String streetNumber, String distance,
			String... sports) {
		Point p = !streetName.isEmpty() ? new Point(getAddressPosition(streetName, streetNumber))
				: new Point(new Position(37.765250, -122.452355));
		double d = !distance.isEmpty() ? Double.parseDouble(distance) : Double.MAX_VALUE;
		String[] s = sports.length == 1 && sports[0].isEmpty() ? new String[] {} : sports;

		return pointRepository.getSportPoints(p, d, s);
	}

	@Override
	public List<CycleRoad> getCycleRoadsData(String lengthFrom, String lengthTo, String filterExcavations) {
		double f = !lengthFrom.isEmpty() ? Double.parseDouble(lengthFrom) : Double.MIN_VALUE;
		double t = !lengthTo.isEmpty() ? Double.parseDouble(lengthTo) : Double.MAX_VALUE;
		boolean e = filterExcavations != null;

		return pointRepository.getCycleRoads(f, t, e);
	}

	@Override
	public List<OutdoorPlayground> getOutdoorPlaygroundsData(String areaFrom, String areaTo, String... surfaces) {
		double f = !areaFrom.isEmpty() ? Double.parseDouble(areaFrom) : Double.MIN_VALUE;
		double t = !areaTo.isEmpty() ? Double.parseDouble(areaTo) : Double.MAX_VALUE;
		String[] s = surfaces.length == 1 && surfaces[0].isEmpty() ? new String[] {} : surfaces;

		return pointRepository.getOutdoorPlaygrounds(f, t, s);
	}

	@Override
	public Set<String> getSports() {
		return pointRepository.getSports();
	}

	@Override
	public Set<String> getSurfaces() {
		return pointRepository.getSurfaces();
	}

	private Position getAddressPosition(String streetName, String streetNumber) {
		RestTemplate restTemplate = new RestTemplate();
		JsonNode response = restTemplate.getForObject("https://maps.googleapis.com/maps/api/geocode/json?address="
				+ streetName + "+" + streetNumber + "&key=AIzaSyAIMXKlqO0IaVdKL8HXCLTejdc7PzVpK50", JsonNode.class);

		JsonNode location = response.findValue("location");
		return new Position(location.get("lat").asDouble(), location.get("lng").asDouble());
	}
}
