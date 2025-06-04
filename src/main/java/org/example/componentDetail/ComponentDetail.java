package org.example.componentDetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * POJO (Plain Old Java Object) for a component’s details,
 * now including an optional list of relations to other components.
 */
@Getter
@NoArgsConstructor
@ToString

public class ComponentDetail {

    private String tags;
    private String technology;
    private String description;
    private String container;

    // NEW: List of outgoing relations from this component
    private List<Relations> relations;

    /**
     * Constructor for ComponentDetail.
     *
     * @param tags        Tags associated with the component.
     * @param technology  Technology used in the component.
     * @param description Description of the component.
     * @param container   (optional) Container reference, if any.
     * @param relations   (optional) List of relations from this component to others.
     */
    @JsonCreator
    public ComponentDetail(
            @JsonProperty("tags") String tags,
            @JsonProperty("technology") String technology,
            @JsonProperty("description") String description,
            @JsonProperty("container") String container,
            @JsonProperty("relations") List<Relations> relations
    ) {
        this.tags = tags;
        this.technology = technology;
        this.description = description;
        this.container = container;
        this.relations = relations;
    }
}
