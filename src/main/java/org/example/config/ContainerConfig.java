package org.example.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;
import org.example.model.ContainerDetail;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Top-level configuration container for managing multiple software containers and their components.
 * 
 * This class represents the root configuration structure for the entire C4 model generation system.
 * It parses and manages a JSON configuration that defines multiple containers (such as "connectorModel",
 * "connectorImplementations", "connectorInfrastructure") and their associated component mappings.
 * 
 * The expected JSON structure:
 * <pre>
 * {
 *   "container": [
 *     {
 *       "connectorModel": {
 *         "objectMapper": [
 *           {
 *             "componentName": "...",
 *             "tags": "...",
 *             "technology": "...",
 *             "description": "...",
 *             "relations": [...]
 *           }
 *         ]
 *       }
 *     },
 *     {
 *       "connectorImplementations": {
 *         "objectMapper": [...]
 *       }
 *     }
 *   ]
 * }
 * </pre>
 * 
 * This class flattens the nested JSON structure into a convenient Map for easy lookup
 * of container configurations by name. It supports the automated C4 model generation
 * process by providing structured access to all container and component definitions.
 * 
 * @author Generated Documentation
 * @version 1.0
 * @since 2025-06-15
 */
@Getter
@ToString
public class ContainerConfig {

    /**
     * Flattened map of container configurations.
     * After JSON binding, this map contains:
     * - key: container name (e.g., "connectorModel", "connectorImplementations")
     * - value: ContainerDetail object containing the component mappings for that container
     */
    private final Map<String, ContainerDetail> containerMap = new HashMap<>();

    /**
     * Creates a ContainerConfig from the parsed JSON container array.
     * 
     * This constructor is used by Jackson for JSON deserialization. It takes the "container"
     * array from the JSON and flattens it into a convenient map structure. Each element in
     * the container array is expected to be a Map with exactly one entry where the key is
     * the container name and the value is the ContainerDetail object.
     * 
     * The flattening process transforms:
     * List<Map<String, ContainerDetail>> → Map<String, ContainerDetail>
     * 
     * @param containers List of single-entry maps from the "container" JSON array
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
     * Convenience factory method for loading container configuration from a JSON file.
     * 
     * This static method provides a simple way to load and parse container configurations
     * directly from a file system path. It handles the Jackson ObjectMapper setup and
     * error handling for file operations.
     * 
     * @param jsonFile File object pointing to the JSON configuration file
     * @return Fully initialized ContainerConfig with all container mappings loaded
     * @throws IOException If the file cannot be read or contains invalid JSON
     */
    public static ContainerConfig loadFromFile(File jsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonFile, ContainerConfig.class);
    }
}
