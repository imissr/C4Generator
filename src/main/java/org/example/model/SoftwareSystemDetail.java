package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;


@Getter
@ToString
public class SoftwareSystemDetail {
    
    /** The display name of the software system (should be business-oriented and recognizable) */
    private final String name;
    
    /** Comprehensive description of the system's purpose, scope, and business value */
    private final String description;
    
    /** List of relationships this system has with other software systems or external entities */
    private final List<Relations> relations;


    @JsonCreator
    public SoftwareSystemDetail(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("relations") List<Relations> relations
    ) {
        this.name = name;
        this.description = description;
        this.relations = relations;
    }
}
