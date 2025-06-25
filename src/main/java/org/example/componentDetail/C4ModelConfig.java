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
 * Enhanced C4 Model Configuration that includes workspace, persons, software systems, and containers.
 * 
 * This class represents the complete configuration structure for automated C4 model generation.
 * It extends the original container-focused configuration to include all C4 model elements:
 * - Workspace metadata (name, description)
 * - Persons/Users with their relationships
 * - Software Systems with their relationships
 * - Containers with their relationships and technology details
 * - Component mappings within each container
 * 
 * The expected JSON structure:
 * <pre>
 * {
 *   "workspace": {
 *     "name": "Avatar C4 Model",
 *     "description": "Component analysis for Avatar Connector System"
 *   },
 *   "persons": [
 *     {
 *       "name": "Client User",
 *       "description": "Uses the Avatar system to access healthcare data",
 *       "relations": [{"target": "Avatar System", "type": "Makes data requests through"}]
 *     }
 *   ],
 *   "softwareSystems": [
 *     {
 *       "name": "Avatar System",
 *       "description": "Connector-based system for healthcare data exchange",
 *       "relations": []
 *     }
 *   ],
 *   "containers": [
 *     {
 *       "name": "Connector API",
 *       "description": "Defines the core contract for connectors",
 *       "technology": "Java/OSGi",
 *       "relations": []
 *     }
 *   ],
 *   "container": [
 *     {
 *       "connectorModel": {
 *         "objectMapper": [...]
 *       }
 *     }
 *   ]
 * }
 * </pre>
 * 
 * @author Generated Documentation
 * @version 1.0
 * @since 2025-06-25
 */
@Getter
@ToString
public class C4ModelConfig {

    private final WorkspaceDetail workspace;
    private final List<PersonDetail> persons;
    private final List<SoftwareSystemDetail> softwareSystems;

    // Conainer should be created
    private final List<ContainerConfigDetail> containers;
    
    // Keep the original container component mappings
    private final Map<String, ContainerDetail> containerComponentMap = new HashMap<>();

    @JsonCreator
    public C4ModelConfig(
            @JsonProperty("workspace") WorkspaceDetail workspace,
            @JsonProperty("persons") List<PersonDetail> persons,
            @JsonProperty("softwareSystems") List<SoftwareSystemDetail> softwareSystems,
            @JsonProperty("containers") List<ContainerConfigDetail> containers,
            @JsonProperty("container") List<Map<String, ContainerDetail>> containerComponents
    ) {
        this.workspace = workspace;
        this.persons = persons != null ? persons : new ArrayList<>();
        this.softwareSystems = softwareSystems != null ? softwareSystems : new ArrayList<>();
        this.containers = containers != null ? containers : new ArrayList<>();
        
        // Process the container component mappings (existing structure)
        if (containerComponents != null) {
            for (Map<String, ContainerDetail> singleContainerEntry : containerComponents) {
                singleContainerEntry.forEach((name, detail) -> {
                    containerComponentMap.put(name, detail);
                });
            }
        }
    }

    /**
     * Convenience factory method for loading complete C4 model configuration from a JSON file.
     * 
     * @param jsonFile File object pointing to the JSON configuration file
     * @return Fully initialized C4ModelConfig with all elements loaded
     * @throws IOException If the file cannot be read or contains invalid JSON
     */
    public static C4ModelConfig loadFromFile(File jsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonFile, C4ModelConfig.class);
    }

    /**
     * Get container component map for backward compatibility.
     * 
     * @return Map of container names to their component details
     */
    public Map<String, ContainerDetail> getContainerMap() {
        return containerComponentMap;
    }
}
