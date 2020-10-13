package eu.miroslavlehotsky.sporttracker.service;

import java.util.List;
import java.util.Set;
import eu.miroslavlehotsky.sporttracker.model.CycleRoad;
import eu.miroslavlehotsky.sporttracker.model.OutdoorPlayground;
import eu.miroslavlehotsky.sporttracker.model.SportPoint;

public interface SportService {

	List<SportPoint> getSportPointData();

	List<SportPoint> getSportPointData(String streetName, String streetNumber, String distance, String... sports);

	List<CycleRoad> getCycleRoadsData(String lengthFrom, String lengthTo, String filterExcavations);

	List<OutdoorPlayground> getOutdoorPlaygroundsData(String areaFrom, String areaTo, String... surfaces);

	Set<String> getSports();

	Set<String> getSurfaces();
}
