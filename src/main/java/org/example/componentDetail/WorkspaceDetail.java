package org.example.componentDetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents the workspace metadata for a C4 architectural model.
 * 
 * <p>The workspace serves as the top-level container for all architectural
 * elements in a C4 model. It provides essential metadata that describes
 * the overall scope, purpose, and context of the architectural documentation
 * being generated.</p>
 * 
 * <p>This class encapsulates the fundamental workspace properties that appear
 * in the generated architectural diagrams and documentation, helping viewers
 * understand what system or domain is being modeled.</p>
 * 
 * <p>Example JSON configuration:</p>
 * <pre>
 * {
 *   "workspace": {
 *     "name": "E-Commerce Platform Architecture",
 *     "description": "Complete architectural model for the online retail system including web storefront, order processing, and inventory management"
 *   }
 * }
 * </pre>
 * 
 * @author C4 Model Generator
 * @version 1.0
 * @since 2025-06-25
 */
@Getter
@ToString
public class WorkspaceDetail {
    
    /** The display name of the workspace/project (appears as the main title in diagrams) */
    private final String name;
    
    /** Comprehensive description explaining the scope and purpose of the architectural model */
    private final String description;

    /**
     * Creates a new workspace definition with the specified metadata.
     * 
     * <p>This constructor is used during JSON deserialization when loading
     * workspace configurations. The workspace represents the top-level
     * context for all architectural elements in the C4 model.</p>
     * 
     * @param name The display name for the workspace (should be concise but descriptive)
     * @param description Detailed explanation of what systems/domain this workspace models
     */
    @JsonCreator
    public WorkspaceDetail(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description
    ) {
        this.name = name;
        this.description = description;
    }
}
