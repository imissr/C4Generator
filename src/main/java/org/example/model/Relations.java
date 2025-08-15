package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@ToString
@NoArgsConstructor
public class Relations {

    /**
     * The name of the target component that this relation points to.
     * This should match the componentName of another ComponentDetail object
     * within the same container or system.
     */
    private String target;

    /**
     * The type or nature of the relationship between the source and target components.
     * Examples: "uses", "depends on", "implements", "extends", "calls", "contains"
     */
    private String type;

    /**
     * Creates a new relationship definition between components.
     *
     * This constructor is used primarily for JSON deserialization when loading
     * component configurations. Both parameters are essential for defining a
     * meaningful relationship in the architectural model.
     *
     * @param target The name of the component that this relationship points to
     * @param type The nature of the relationship (e.g., "uses", "depends on", "implements")
     */
    @JsonCreator
    public Relations(
            @JsonProperty("target") String target,
            @JsonProperty("type") String type
    ) {
        this.target = target;
        this.type = type;
    }
}
