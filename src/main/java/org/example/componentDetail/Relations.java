package org.example.componentDetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Represents a directed relationship between two components in the system architecture.
 * 
 * This class models the connections and dependencies between software components,
 * enabling the C4 model generator to create accurate architectural diagrams with
 * proper relationship visualization. Each relation defines a source component
 * (implicitly the component that contains this relation) and a target component
 * with a specific relationship type.
 * 
 * Relations are essential for understanding:
 * - Component dependencies and coupling
 * - Data flow between components  
 * - Service interactions and API usage
 * - Architectural boundaries and communication patterns
 * 
 * Example usage in JSON:
 * <pre>
 * {
 *   "componentName": "User Service",
 *   "relations": [
 *     {
 *       "target": "Database Connection",
 *       "type": "uses"
 *     },
 *     {
 *       "target": "Authentication Service", 
 *       "type": "depends on"
 *     }
 *   ]
 * }
 * </pre>
 * 
 * Common relationship types include:
 * - "uses" - Component actively uses another component's services
 * - "depends on" - Component requires another component to function
 * - "implements" - Component implements an interface defined by another
 * - "extends" - Component extends functionality of another component
 * - "calls" - Component makes direct calls to another component
 * 
 * @author Generated Documentation
 * @version 1.0
 * @since 2025-06-15
 */
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
