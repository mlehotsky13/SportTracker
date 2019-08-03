package sk.stuba.fiit.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LineString {
    private List<Position> coordinates;

    public void addCoordinates(Position position) {
        coordinates.add(position);
    }
    
    @Override
    public String toString() {
        return "{\"type\": \"LineString\", \"coordinates\": " + coordinates + "}";
    }
}
