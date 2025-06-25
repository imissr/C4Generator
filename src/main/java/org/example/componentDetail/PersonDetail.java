package org.example.componentDetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Configuration for a Person element in the C4 model.
 * 
 * This class represents a person (user/actor) in the system with their
 * basic properties and relationships to other elements.
 * 
 * @author Generated Documentation
 * @version 1.0
 * @since 2025-06-25
 */
@Getter
@ToString
public class PersonDetail {
    private final String name;
    private final String description;
    private final List<Relations> relations;

    @JsonCreator
    public PersonDetail(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("relations") List<Relations> relations
    ) {
        this.name = name;
        this.description = description;
        this.relations = relations;
    }
}
