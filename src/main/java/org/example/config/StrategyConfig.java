package org.example.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


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

}
