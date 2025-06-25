package org.example.componentDetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Configuration for a Container element in the C4 model.
 * 
 * This class represents a container with its properties, technology,
 * and relationships to other containers.
 * 
 * @author Generated Documentation
 * @version 1.0
 * @since 2025-06-25
 */
@Getter
@ToString
public class ContainerConfigDetail {
    private final String name;
    private final String description;
    private final String technology;
    private final List<Relations> relations;

    @JsonCreator
    public ContainerConfigDetail(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("technology") String technology,
            @JsonProperty("relations") List<Relations> relations
    ) {
        this.name = name;
        this.description = description;
        this.technology = technology;
        this.relations = relations;
    }
}
