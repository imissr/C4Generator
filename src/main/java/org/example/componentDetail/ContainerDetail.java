package org.example.componentDetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the detailed configuration and component mappings for a single container
 * within the Avatar system architecture.
 *
 * This class manages the component mappings for a specific architectural container
 * (such as "connectorModel", "connectorImplementations", or "connectorInfrastructure").
 * It holds a list of ComponentDetail objects that represent all the components
 * within this container and provides utility methods for easy access and lookup.
 *
 * The class supports the updated JSON format where "objectMapper" is an array of
 * ComponentDetail objects rather than a map structure. This provides more flexibility
 * in component ordering and configuration.
 *
 * Example JSON snippet for a container:
 * <pre>
 * {
 *   "objectMapper": [
 *     {
 *       "componentName": "Connector Info",
 *       "tags": "Info",
 *       "technology": "EMF Model",
 *       "description": "Provides metadata about connectors",
 *       "relations": [...]
 *     },
 *     {
 *       "componentName": "Connector Endpoint",
 *       "tags": "Endpoint",
 *       "technology": "EMF Model",
 *       "description": "Defines connection endpoints"
 *     }
 *   ]
 * }
 * </pre>
 *
 * @author Generated Documentation
 * @version 1.0
 * @since 2025-06-15
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ContainerDetail {

    /**
     * List of component configurations within this container.
     * In the JSON structure, this corresponds to the "objectMapper" array containing
     * all ComponentDetail objects for this specific container.
     */
    @JsonProperty("objectMapper")
    private List<ComponentDetail> objectMapper = new ArrayList<>();

    /**
     * Creates a ContainerDetail with the specified component mappings.
     *
     * This constructor is used primarily for JSON deserialization when loading
     * container configurations from external files. It initializes the container
     * with a list of component details that define all components within this
     * architectural container.
     *
     * @param objectMapper List of ComponentDetail objects representing all components in this container
     */
    @JsonCreator
    public ContainerDetail(
            @JsonProperty("objectMapper") List<ComponentDetail> objectMapper
    ) {
        this.objectMapper = (objectMapper == null) ? new ArrayList<>() : objectMapper;
    }

    /**
     * Creates a convenient lookup map from the component list.
     *
     * This utility method transforms the internal list of ComponentDetail objects
     * into a Map where component names serve as keys. This enables fast lookup
     * of specific components by name during the C4 model generation process.
     *
     * The mapping uses the ComponentDetail.componentName field as the key,
     * so component names should be unique within each container.
     *
     * @return Map where keys are component names and values are ComponentDetail objects
     * @throws IllegalStateException If duplicate component names are found within the container
     */
    public Map<String, ComponentDetail> getComponentMap() {
        return objectMapper.stream()
                .collect(Collectors.toMap(
                        ComponentDetail::getComponentName,
                        entry -> entry
                ));
    }
}
