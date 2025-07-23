package org.example.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Defines a component discovery strategy for the configurable C4 model generation system.
 * 
 * <p>This class represents a single, configurable strategy that defines how software
 * components should be automatically discovered and documented within specific containers.
 * Each strategy encapsulates the logic for finding components based on various criteria
 * such as annotations, naming patterns, or custom rules.</p>
 * 
 * <p>The strategy system supports multiple discovery approaches:</p>
 * <ul>
 *   <li><strong>ANNOTATION</strong> - Discovers components based on Java annotations</li>
 *   <li><strong>REGEX</strong> - Uses regular expressions to match class names</li>
 *   <li><strong>NAME_SUFFIX</strong> - Matches classes ending with specific suffixes</li>
 *   <li><strong>CUSTOM_ANNOTATION</strong> - Uses custom annotation-based discovery logic</li>
 * </ul>
 * 
 * <p>Example JSON configuration:</p>
 * <pre>
 * {
 *   "name": "Service Components",
 *   "type": "ANNOTATION",
 *   "config": {
 *     "annotationType": "org.springframework.stereotype.Service"
 *   },
 *   "containerMapping": "applicationServices",
 *   "enabled": true
 * }
 * </pre>
 * 
 * @author C4 Model Generator
 * @version 1.0
 * @since 2025-06-26
 */
@Getter
@Setter
public class StrategyConfig {
    
    /** Human-readable name describing what this strategy discovers (e.g., "Spring Services", "Repository Classes") */
    @JsonProperty("name")
    private String name;
    
    /** 
     * The type of discovery strategy to use.
     * Determines which component matching algorithm will be applied during scanning.
     */
    @JsonProperty("type")
    private StrategyType type;
    
    /** 
     * Strategy-specific configuration parameters.
     * Content varies by strategy type (e.g., annotation class names, regex patterns, suffixes).
     */
    @JsonProperty("config")
    private Map<String, Object> config;
    
    /** 
     * Identifier of the target container where discovered components should be placed.
     * Must match a container name defined in the C4 model configuration.
     */
    @JsonProperty("containerMapping")
    private String containerMapping;
    
    /** Flag indicating whether this strategy should be executed during component discovery */
    @JsonProperty("enabled")
    private boolean enabled = true;
    
    /**
     * Enum defining the available strategy types
     */
    public enum StrategyType {
        ANNOTATION,        // Uses AnnotationTypeMatcher
        REGEX,            // Uses RegexTypeMatcher  
        NAME_SUFFIX,      // Uses NameSuffixTypeMatcher
        CUSTOM_ANNOTATION // Uses custom NewComponentStrategy
    }
    
    /**
     * Get configuration value as String
     */
    public String getConfigString(String key) {
        Object value = config.get(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * Get configuration value as Boolean
     */
    public Boolean getConfigBoolean(String key) {
        Object value = config.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }
}
