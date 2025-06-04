package org.example.componentDetail;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ComponentMapperLoader {

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
