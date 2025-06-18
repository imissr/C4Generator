package org.example.componentDetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Configuration class for defining component discovery strategies.
 * 
 * This class represents a single strategy configuration that defines how components
 * should be discovered within a specific container. Each strategy has a type
 * (ANNOTATION, REGEX, NAME_SUFFIX, CUSTOM_ANNOTATION) and associated configuration
 * parameters that control the discovery behavior.
 */
@Getter
@Setter
public class StrategyConfig {
    
    /**
     * Human-readable name for the strategy
     */
    @JsonProperty("name")
    private String name;
    
    /**
     * Type of strategy - determines which TypeMatcher implementation to use
     * Valid values: ANNOTATION, REGEX, NAME_SUFFIX, CUSTOM_ANNOTATION
     */
    @JsonProperty("type")
    private StrategyType type;
    
    /**
     * Description of what this strategy discovers
     */
    @JsonProperty("description")
    private String description;
    
    /**
     * Configuration parameters specific to the strategy type
     */
    @JsonProperty("config")
    private Map<String, Object> config;
    
    /**
     * Which container this strategy should populate
     */
    @JsonProperty("containerMapping")
    private String containerMapping;
    
    /**
     * Technology tag to apply to discovered components
     */
    @JsonProperty("technology")
    private String technology;
    
    /**
     * Whether this strategy is enabled
     */
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
