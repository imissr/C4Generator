package org.example.componentDetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Now that `objectMapper` is an array of ComponentEntry objects,
 * we switch from Map<String, ComponentDetail> to List<ComponentEntry>.
 *
 * Example JSON snippet for a single container in the new format:
 *
 * {
 *   "objectMapper": [
 *     {
 *       "componentName": "Connector Info",
 *       "tags": "Info",
 *       "technology": "EMF Model",
 *       "description": "...",
 *       "relations": [ … ]
 *     },
 *     {
 *       "componentName": "Connector Endpoint",
 *       "tags": "Endpoint",
 *       "technology": "EMF Model",
 *       "description": "…"
 *     },
 *     …
 *   ]
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ContainerDetail {

    /**
     * In your JSON, "objectMapper" is now an array of ComponentEntry (not a Map).
     */
    @JsonProperty("objectMapper")
    private List<ComponentDetail> objectMapper = new ArrayList<>();

    @JsonCreator
    public ContainerDetail(
            @JsonProperty("objectMapper") List<ComponentDetail> objectMapper
    ) {
        this.objectMapper = (objectMapper == null) ? new ArrayList<>() : objectMapper;
    }

    /**
     * Utility method: Return a Map where
     *   • key = componentName
     *   • value = that ComponentEntry instance
     *
     * This makes it easy to look up any ComponentEntry by its name.
     */
    public Map<String, ComponentDetail> getComponentMap() {
        return objectMapper.stream()
                .collect(Collectors.toMap(
                        ComponentDetail::getComponentName,
                        entry -> entry
                ));
    }
}
