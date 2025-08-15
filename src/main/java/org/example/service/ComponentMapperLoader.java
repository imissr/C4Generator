package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.ComponentDetail;

import java.io.File;
import java.util.Map;


public class ComponentMapperLoader {

    /**
     * Loads a component mapping from a JSON file.
     * 
     * This method parses the specified JSON file and extracts the component mapping
     * from the "componentMapper" node. The mapping associates component identifiers
     * with their complete ComponentDetail configurations.
     * 
     * @param jsonPath Absolute or relative path to the JSON configuration file
     * @return Map where keys are component identifiers and values are ComponentDetail objects
     * @throws Exception If the file cannot be read, parsed, or doesn't contain the expected structure
     * @throws IllegalStateException If the "componentMapper" node is missing or invalid
     */
    public static Map<String, ComponentDetail> loadComponentMap(String jsonPath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(jsonPath));
        JsonNode mapperNode = root.path("componentMapper");
        if (!mapperNode.isObject()) {
            throw new IllegalStateException("Missing or invalid 'componentMapper' node");
        }

        return mapper.convertValue(
                mapperNode,
                new TypeReference<Map<String, ComponentDetail>>() {}
        );
    }
}
