package org.example.componentDetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Represents a person (user, actor, or stakeholder) in the C4 architectural model.
 * 
 * <p>This class models human actors who interact with the software systems
 * being documented. In C4 architecture, persons represent the users who
 * ultimately drive the need for the software systems and define the context
 * in which those systems operate.</p>
 * 
 * <p>PersonDetail instances are typically used to:</p>
 * <ul>
 *   <li>Define system users and their roles</li>
 *   <li>Establish relationships between users and systems/containers</li>
 *   <li>Document user journeys and interaction patterns</li>
 *   <li>Provide context for system boundaries and scope</li>
 * </ul>
 * 
 * <p>Example JSON configuration:</p>
 * <pre>
 * {
 *   "name": "Customer Support Agent",
 *   "description": "Provides customer assistance and resolves technical issues",
 *   "relations": [
 *     {
 *       "target": "Support Dashboard",
 *       "type": "uses"
 *     },
 *     {
 *       "target": "Customer Database",
 *       "type": "reads from"
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
public class PersonDetail {
    
    /** The display name of the person/user/actor (e.g., "Customer", "Administrator", "API Client") */
    private final String name;
    
    /** Detailed description of the person's role, responsibilities, and context within the system */
    private final String description;
    
    /** List of relationships this person has with software systems, containers, or other elements */
    private final List<Relations> relations;

    /**
     * Creates a new person definition for the C4 model.
     * 
     * <p>This constructor is primarily used during JSON deserialization when
     * loading person configurations from external files. The person represents
     * a human actor in the system context.</p>
     * 
     * @param name The display name for this person (should be concise and descriptive)
     * @param description Detailed explanation of the person's role and how they interact with the system
     * @param relations Optional list of relationships this person has with system elements (can be null)
     */
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
