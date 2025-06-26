package org.example.componentDetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Represents a software system in the C4 architectural model.
 * 
 * <p>A software system is the highest level of abstraction in the C4 model,
 * representing something that delivers value to its users. Software systems
 * are composed of one or more containers, which in turn contain components.
 * This level focuses on the overall system purpose and its relationships
 * with other systems and external entities.</p>
 * 
 * <p>Software systems typically represent:</p>
 * <ul>
 *   <li>Complete applications or services that deliver business value</li>
 *   <li>Third-party systems that your system integrates with</li>
 *   <li>Legacy systems that are part of the enterprise landscape</li>
 *   <li>External services and APIs consumed by your system</li>
 * </ul>
 * 
 * <p>Example JSON configuration:</p>
 * <pre>
 * {
 *   "name": "Customer Management System",
 *   "description": "Centralized system for managing customer data, preferences, and interaction history",
 *   "relations": [
 *     {
 *       "target": "Payment Processing System",
 *       "type": "integrates with"
 *     },
 *     {
 *       "target": "Email Marketing Platform",
 *       "type": "sends data to"
 *     }
 *   ]
 * }
 * </pre>
 * 
 * @author C4 Model Generator
 * @version 1.0
 * @since 2025-06-25
 */
@Getter
@ToString
public class SoftwareSystemDetail {
    
    /** The display name of the software system (should be business-oriented and recognizable) */
    private final String name;
    
    /** Comprehensive description of the system's purpose, scope, and business value */
    private final String description;
    
    /** List of relationships this system has with other software systems or external entities */
    private final List<Relations> relations;

    /**
     * Creates a new software system definition for the C4 model.
     * 
     * <p>This constructor is used during JSON deserialization when loading
     * system configurations. Software systems represent the highest level
     * of abstraction in C4 modeling, focusing on business capabilities
     * and system boundaries.</p>
     * 
     * @param name The business-oriented name for this software system
     * @param description Detailed explanation of the system's purpose, scope, and business value
     * @param relations Optional list of relationships this system has with other systems (can be null)
     */
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
