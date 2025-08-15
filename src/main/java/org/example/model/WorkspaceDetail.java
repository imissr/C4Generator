package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class WorkspaceDetail {
    
    /** The display name of the workspace/project (appears as the main title in diagrams) */
    private final String name;
    
    /** Comprehensive description explaining the scope and purpose of the architectural model */
    private final String description;


    @JsonCreator
    public WorkspaceDetail(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description
    ) {
        this.name = name;
        this.description = description;
    }
}
