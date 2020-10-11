package eu.miroslavlehotsky.sporttracker.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.miroslavlehotsky.sporttracker.model.CycleRoad;
import eu.miroslavlehotsky.sporttracker.model.OutdoorPlayground;
import eu.miroslavlehotsky.sporttracker.model.SportPoint;
import eu.miroslavlehotsky.sporttracker.service.SportService;
import eu.miroslavlehotsky.sporttracker.util.Utils;

@Controller
public class IndexController {

	@Autowired
	private SportService sportService;

	@GetMapping("/")
	public String getAllSportFacilities(Model model) throws SQLException {
		List<SportPoint> sportPoints = sportService.getSportPointData();

		String sportPointData = Utils.getFeatureCollection(sportPoints);
		Set<String> sports = sportService.getSports();
		Set<String> surfaces = sportService.getSurfaces();

		model.addAttribute("mapData", sportPointData);
		model.addAttribute("sports", sports);
		model.addAttribute("surfaces", surfaces);

		return "index";
	}

	@GetMapping("/facilities")
	public String filterSportFacilities(//
			@RequestParam("streetName") String streetName, //
			@RequestParam("streetNumber") String streetNumber, //
			@RequestParam("distance") String distance, //
			@RequestParam(value = "sport", required = false) String sport, //
			Model model) throws SQLException {

		List<SportPoint> sportPoints = sportService.getSportPointData(streetName, streetNumber, distance,
				Optional.ofNullable(sport).orElse(""));

		String sportPointData = Utils.getFeatureCollection(sportPoints);
		Set<String> sports = sportService.getSports();
		Set<String> surfaces = sportService.getSurfaces();

		model.addAttribute("mapData", sportPointData);
		model.addAttribute("sports", sports);
		model.addAttribute("surfaces", surfaces);

		// return request parameters to set elements appropriately afterward
		model.addAttribute("streetName", streetName);
		model.addAttribute("streetNumber", streetNumber);
		model.addAttribute("distance", distance);
		model.addAttribute("selectedSport", sport);

		return "index";
	}

	@GetMapping("/playgrounds")
	public String getOutdoorPlaygrounds(//
			@RequestParam(value = "surface", required = false) String surface, //
			@RequestParam("areaFrom") String areaFrom, //
			@RequestParam("areaTo") String areaTo, //
			Model model) {

		List<OutdoorPlayground> outdoorPlaygrounds = sportService.getOutdoorPlaygroundsData(areaFrom, areaTo,
				Optional.ofNullable(surface).orElse(""));

		String outdoorPlaygroundsData = Utils.getFeatureCollection(outdoorPlaygrounds);
		Set<String> sports = sportService.getSports();
		Set<String> surfaces = sportService.getSurfaces();

		model.addAttribute("mapData", outdoorPlaygroundsData);
		model.addAttribute("sports", sports);
		model.addAttribute("surfaces", surfaces);

		model.addAttribute("areaFrom", areaFrom);
		model.addAttribute("areaTo", areaTo);
		model.addAttribute("selectedSurface", surface);

		return "index";
	}

	@GetMapping("/cycling-routes")
	public String getCycleRoads(//
			@RequestParam("lengthFrom") String lengthFrom, //
			@RequestParam("lengthTo") String lengthTo, //
			@RequestParam(value = "filterExcavations", required = false) String filterExcavations, //
			Model model) {

		List<CycleRoad> cycleRoads = sportService.getCycleRoadsData(lengthFrom, lengthTo, filterExcavations);

		String cycleRoadsData = Utils.getFeatureCollection(cycleRoads);
		Set<String> sports = sportService.getSports();
		Set<String> surfaces = sportService.getSurfaces();

		model.addAttribute("mapData", cycleRoadsData);
		model.addAttribute("sports", sports);
		model.addAttribute("surfaces", surfaces);

		// return request parameters to set elements appropriately afterward
		model.addAttribute("lengthFrom", lengthFrom);
		model.addAttribute("lengthTo", lengthTo);
		model.addAttribute("filterExcavations", filterExcavations != null);

		return "index";
	}
}
