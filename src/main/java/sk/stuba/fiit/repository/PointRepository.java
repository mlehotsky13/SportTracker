package sk.stuba.fiit.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import sk.stuba.fiit.model.CycleRoad;
import sk.stuba.fiit.model.LineString;
import sk.stuba.fiit.model.OutdoorPlayground;
import sk.stuba.fiit.model.Point;
import sk.stuba.fiit.model.Polygon;
import sk.stuba.fiit.model.Position;
import sk.stuba.fiit.model.SportPoint;

@Repository
public class PointRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    public List<SportPoint> getSportPoints(Point point, double distance, String... sports) {
        String sql = "SELECT osm_id, name, leisure, sport, ST_AsGeoJSON(ST_Transform(way, 4326)) AS geo_json";
        sql += " FROM planet_osm_point";
        sql += " WHERE ST_DWithin(ST_Transform(way, 4326)::geography, ST_GeomFromGeoJSON('"
                + point.reverseCoordinatestoString() + "')::geography, " + distance + ")";
        sql += " AND STRING_TO_ARRAY(sport, ';') @> ARRAY[";
        sql += Arrays.asList(sports).stream().map(v -> "'" + v + "'").collect(Collectors.joining(", "));
        sql += "]::text[]";
        sql += " UNION";
        sql += " SELECT osm_id, name, leisure, sport, ST_AsGeoJSON(ST_Transform(ST_Centroid(way), 4326)) AS geo_json";
        sql += " FROM planet_osm_polygon";
        sql += " WHERE ST_DWithin(ST_Transform(ST_Centroid(way), 4326)::geography, ST_GeomFromGeoJSON('"
                + point.reverseCoordinatestoString() + "')::geography, " + distance + ")";
        sql += " AND STRING_TO_ARRAY(sport, ';') @> ARRAY[";
        sql += Arrays.asList(sports).stream().map(v -> "'" + v + "'").collect(Collectors.joining(", "));
        sql += "]::text[]";

        return executeAndCollectSportPoints(sql);
    }

    public List<OutdoorPlayground> getOutdoorPlaygrounds(double areaFrom, double areaTo, String... surfaces) {
        String sql = "SELECT osm_id, name, surface, ST_AsGeoJSON(ST_Transform(way, 4326)) AS geo_json";
        sql += " FROM planet_osm_polygon";
        sql += " WHERE leisure = 'pitch'";
        sql += " AND ST_Area(ST_Transform(way, 4326)::geography) >= " + areaFrom;
        sql += " AND ST_Area(ST_Transform(way, 4326)::geography) <= " + areaTo;
        sql += Arrays.asList(surfaces).stream().map(v -> " AND surface = '" + v + "'").collect(Collectors.joining(""));

        return executeAndCollectOutoorPlaygrounds(sql);
    }

    public List<CycleRoad> getCycleRoads(double lengthFrom, double lengthTo, boolean filterExcavations) {
        String sql = "WITH excavations_geo as (";
        sql += " SELECT way::geography as excavation_geo";
        sql += " FROM excavations";
        sql += " WHERE permit_start_date <= now()";
        sql += " AND permit_end_date >= now()";
        sql += " AND permit_status = 'ACTIVE' )";
        sql += " , lines_geo AS (";
        sql += " SELECT osm_id, name, ST_Transform(way, 4326)::geography AS line_geo";
        sql += " FROM planet_osm_line";
        sql += " WHERE route LIKE 'bicycle'";
        sql += " AND ST_Length(ST_Transform(way, 4326)::geography) >= " + lengthFrom;
        sql += " AND ST_Length(ST_Transform(way, 4326)::geography) <= " + lengthTo + ")";
        sql += " SELECT osm_id, name, ST_AsGeoJSON(line_geo) AS geo_json";
        sql += " FROM lines_geo";

        if (filterExcavations) {
            sql += " EXCEPT";
            sql += " SELECT DISTINCT osm_id, name, ST_AsGeoJSON(lines_geo.line_geo) AS geo_json";
            sql += " FROM excavations_geo CROSS JOIN lines_geo";
            sql += " WHERE ST_DWithin(lines_geo.line_geo, excavations_geo.excavation_geo, 5)";
        }

        return executeAndCollectCycleRoads(sql);
    }

    public Set<String> getSports() {
        return executeAndCollectSports("SELECT sport FROM sports");
    }

    public Set<String> getSurfaces() {
        return executeAndCollectSurfaces("SELECT surface FROM surfaces");
    }

    private List<SportPoint> executeAndCollectSportPoints(String sql) {
        List<SportPoint> sportPoints = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> row : rows) {
            SportPoint sportPoint = new SportPoint();

            sportPoint.setId((long) row.get("osm_id"));
            sportPoint.setName((String) row.get("name"));
            sportPoint.setLeisure((String) row.get("leisure"));
            sportPoint.setSports(new HashSet<>(Arrays.asList(((String) row.get("sport")).split("[;,]"))));
            sportPoint.setPoint(getPoint((String) row.get("geo_json")));

            sportPoints.add(sportPoint);
        }

        return sportPoints;
    }

    private List<CycleRoad> executeAndCollectCycleRoads(String sql) {
        List<CycleRoad> cycleRoads = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> row : rows) {
            CycleRoad cycleRoad = new CycleRoad();

            cycleRoad.setId((long) row.get("osm_id"));
            cycleRoad.setName((String) row.get("name"));
            cycleRoad.setLineString(getLineString((String) row.get("geo_json")));

            cycleRoads.add(cycleRoad);
        }

        return cycleRoads;
    }

    private Set<String> executeAndCollectSports(String sql) {
        Set<String> sports = new TreeSet<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> row : rows) {
            sports.add((String) row.get("sport"));
        }

        return sports;
    }

    private Set<String> executeAndCollectSurfaces(String sql) {
        Set<String> surfaces = new TreeSet<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> row : rows) {
            surfaces.add((String) row.get("surface"));
        }

        return surfaces;
    }

    private List<OutdoorPlayground> executeAndCollectOutoorPlaygrounds(String sql) {
        List<OutdoorPlayground> outdoorPlaygrounds = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> row : rows) {
            OutdoorPlayground outdoorPlayground = new OutdoorPlayground();

            outdoorPlayground.setId((long) row.get("osm_id"));
            outdoorPlayground.setName((String) row.get("name"));
            outdoorPlayground.setSurface((String) row.get("surface"));
            outdoorPlayground.setPolygon(getPolygon((String) row.get("geo_json")));

            outdoorPlaygrounds.add(outdoorPlayground);
        }

        return outdoorPlaygrounds;
    }

    private Point getPoint(String geoJson) {
        Point point = null;

        try {
            JsonNode geoJsonNode = objectMapper.readTree(geoJson);
            point = new Point(getPosition((ArrayNode) geoJsonNode.get("coordinates")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return point;
    }

    private LineString getLineString(String geoJson) {
        LineString lineString = null;

        try {
            JsonNode geoJsonNode = objectMapper.readTree(geoJson);
            ArrayNode coordinatesNode = (ArrayNode) geoJsonNode.get("coordinates");
            List<Position> coordinates = getPositions(coordinatesNode);

            lineString = new LineString(coordinates);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lineString;
    }

    private Polygon getPolygon(String geoJson) {
        Polygon polygon = null;

        try {
            JsonNode geoJsonNode = objectMapper.readTree(geoJson);
            ArrayNode coordinatesNode = (ArrayNode) geoJsonNode.get("coordinates").get(0);
            List<Position> coordinates = getPositions(coordinatesNode);

            polygon = new Polygon(coordinates);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return polygon;
    }

    private List<Position> getPositions(ArrayNode coordinatesNode) {
        List<Position> positions = new ArrayList<>();

        for (JsonNode node : coordinatesNode) {
            positions.add(getPosition((ArrayNode) node));
        }

        return positions;
    }

    private Position getPosition(ArrayNode coordinatesNode) {
        return new Position(coordinatesNode.get(0).asDouble(), coordinatesNode.get(1).asDouble());
    }
}
