package org.example.componentDetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Top-level holder for the entire JSON structure:
 *
 * {
 *   "container": [
 *     {
 *       "connectorModel": {
 *         "objectMapper": [ … ]
 *       }
 *     },
 *     {
 *       "anotherContainerName": {
 *         "objectMapper": [ … ]
 *       }
 *     }
 *   ]
 * }
 *
 * We want to flatten that into a Map<String, ContainerDetail> so that
 * you can do config.getContainerMap().get("connectorModel") → ContainerDetail
 */
@Getter
@ToString
public class ContainerConfig {

    /**
     * After binding, this map will hold:
     *   key   = containerName (e.g. "connectorModel")
     *   value = the ContainerDetail object (with a List<ComponentEntry> inside)
     */
    private final Map<String, ContainerDetail> containerMap = new HashMap<>();

    /**
     * We know the JSON has a top-level property "container" whose value is an array of
     * objects.  Each of those objects has exactly one key: the container's name.  Its value
     * is a ContainerDetail (with objectMapper array inside).
     *
     * By declaring this constructor with @JsonCreator, Jackson will pass us:
     *   List< Map< String, ContainerDetail > >  – i.e. each list entry is a Map of size 1.
     */
    @JsonCreator
    public ContainerConfig(
            @JsonProperty("container")
            List<Map<String, ContainerDetail>> containers
    ) {
        if (containers != null) {
            for (Map<String, ContainerDetail> singleContainerEntry : containers) {
                // Each `singleContainerEntry` should have exactly one (name → detail) pair.
                singleContainerEntry.forEach((name, detail) -> {
                    // Put into our flat containerMap
                    containerMap.put(name, detail);
                });
            }
        }
    }

    /**
     * Convenience: load from a JSON file path.
     */
    public static ContainerConfig loadFromFile(File jsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonFile, ContainerConfig.class);
    }
}
