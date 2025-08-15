package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;


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
