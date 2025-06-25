package org.example.componentDetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

/**
 * Configuration for workspace metadata in the C4 model.
 * 
 * This class represents the workspace/project level configuration
 * including name and description.
 * 
 * @author Generated Documentation
 * @version 1.0
 * @since 2025-06-25
 */
@Getter
@ToString
public class WorkspaceDetail {
    private final String name;
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
