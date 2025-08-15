package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@Getter
@NoArgsConstructor
@ToString
public class ComponentDetail {

    /** Comma-separated tags for categorizing and styling the component (e.g., "API,Interface,Core") */
    private String tags;

    /** Technology stack or framework used by this component (e.g., "Java/OSGi", "EMF Model") */
    private String technology;

    /** Human-readable description explaining the component's purpose and functionality */
    private String description;

    /** Unique identifier name for this component within its container */
    private String componentName;

    /** List of outgoing relationships from this component to other components in the system */
    private List<Relations> relations;

    /**
     * Creates a new ComponentDetail instance with complete component metadata.
     *
     * This constructor is used primarily for JSON deserialization when loading
     * component configurations from external configuration files. All parameters
     * are optional and can be null, though componentName should typically be provided
     * for proper component identification.
     *
     * @param componentName Unique identifier for the component within its container
     * @param tags Comma-separated categorization tags (e.g., "API,Core,Interface")
     * @param technology Technology stack description (e.g., "Java/OSGi", "EMF Model")
     * @param description Human-readable explanation of component purpose and functionality
     * @param relations List of relationships this component has to other components (can be null)
     */
    @JsonCreator
    public ComponentDetail(
            @JsonProperty("componentName") String componentName,
            @JsonProperty("tags") String tags,
            @JsonProperty("technology") String technology,
            @JsonProperty("description") String description,
            @JsonProperty("relations") List<Relations> relations
    ) {
        this.tags = tags;
        this.technology = technology;
        this.description = description;
        this.componentName = componentName;
        this.relations = relations;
    }
}
