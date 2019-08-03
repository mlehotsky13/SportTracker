package sk.stuba.fiit.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import sk.stuba.fiit.model.CycleRoad;
import sk.stuba.fiit.model.OutdoorPlayground;
import sk.stuba.fiit.model.Point;
import sk.stuba.fiit.model.Position;
import sk.stuba.fiit.model.SportPoint;
import sk.stuba.fiit.repository.PointRepository;

@Service
public class SportService {

    @Autowired
    private PointRepository pointRepository;

    public List<SportPoint> getSportPointData() {
        return getSportPointData("", "", "");
    }

    public List<SportPoint> getSportPointData(String streetName, String streetNumber, String distance,
            String... sports) {
        Point p = !streetName.isEmpty() ? new Point(getAddressPosition(streetName, streetNumber))
                : new Point(new Position(37.765250, -122.452355));
        double d = !distance.isEmpty() ? Double.parseDouble(distance) : Double.MAX_VALUE;
        String[] s = sports.length == 1 && sports[0].isEmpty() ? new String[] {} : sports;

        return pointRepository.getSportPoints(p, d, s);
    }

    public List<CycleRoad> getCycleRoadsData(String lengthFrom, String lengthTo, String filterExcavations) {
        double f = !lengthFrom.isEmpty() ? Double.parseDouble(lengthFrom) : Double.MIN_VALUE;
        double t = !lengthTo.isEmpty() ? Double.parseDouble(lengthTo) : Double.MAX_VALUE;
        boolean e = filterExcavations != null;

        return pointRepository.getCycleRoads(f, t, e);
    }

    public List<OutdoorPlayground> getOutdoorPlaygroundsData(String areaFrom, String areaTo, String... surfaces) {
        double f = !areaFrom.isEmpty() ? Double.parseDouble(areaFrom) : Double.MIN_VALUE;
        double t = !areaTo.isEmpty() ? Double.parseDouble(areaTo) : Double.MAX_VALUE;
        String[] s = surfaces.length == 1 && surfaces[0].isEmpty() ? new String[] {} : surfaces;

        return pointRepository.getOutdoorPlaygrounds(f, t, s);
    }

    public Set<String> getSports() {
        return pointRepository.getSports();
    }

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
