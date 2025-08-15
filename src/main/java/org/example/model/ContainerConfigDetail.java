package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;


@Getter
@ToString
public class ContainerConfigDetail {
    
    /** The display name of the container (should reflect its primary purpose or technology) */
    private final String name;

    private final String softwareSystemName;
    
    /** Detailed description of the container's role, responsibilities, and functionality */
    private final String description;
    
    /** Technology stack, framework, or runtime environment used by this container */
    private final String technology;
    
    /** List of relationships this container has with other containers or external systems */
    private final List<Relations> relations;

    /**
     * Creates a new container definition for the C4 model.
     * 
     * <p>This constructor is used during JSON deserialization when loading
     * container configurations. Containers represent the deployable/runtime
     * units that make up a software system, with a focus on technology
     * choices and deployment characteristics.</p>
     * 
     * @param name The display name for this container (should be descriptive of its purpose)
     * @param description Detailed explanation of the container's role and responsibilities
     * @param technology The technology stack, framework, or runtime environment (e.g., "Java/Spring Boot", "React/TypeScript")
     * @param relations Optional list of relationships this container has with other containers (can be null)
     */
    @JsonCreator
    public ContainerConfigDetail(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("technology") String technology,
            @JsonProperty("relations") List<Relations> relations,
            @JsonProperty("softwareSystemName") String softwareSystemName
    ) {
        this.name = name;
        this.description = description;
        this.technology = technology;
        this.relations = relations;
        this.softwareSystemName = softwareSystemName;
    }
}
