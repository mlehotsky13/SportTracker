package eu.miroslavlehotsky.sporttracker.util;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Utils {

    public static String getFeatureCollection(List<?> items) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();

        try {
            ArrayNode features = objectMapper.createArrayNode();
            for (Object item : items) {
                features.add(objectMapper.readTree(item.toString()));
            }

            objectNode.put("type", "FeautureCollection");
            objectNode.set("features", features);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return objectNode.toString();
    }
}
