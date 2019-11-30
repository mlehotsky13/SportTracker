package eu.miroslavlehotsky.sporttracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Point {
    private Position coordinates;

    @Override
    public String toString() {
        return "{\"type\": \"Point\", \"coordinates\": " + coordinates + "}";
    }

    public String reverseCoordinatestoString() {
        return "{\"type\": \"Point\", \"coordinates\": " + coordinates.reverseCoordinatesToString() + "}";
    }
}
