package org.example.componentDetail;

import com.structurizr.component.matcher.AnnotationTypeMatcher;
import com.structurizr.component.matcher.NameSuffixTypeMatcher;
import com.structurizr.component.matcher.RegexTypeMatcher;
import com.structurizr.component.matcher.TypeMatcher;
import org.example.c4.NewComponentStratgy;

/**
 * Factory class for creating TypeMatcher instances from strategy configurations.
 * 
 * This factory converts JSON-based strategy configurations into actual TypeMatcher
 * implementations that can be used by Structurizr's ComponentFinder. It supports
 * all the built-in Structurizr matchers as well as custom implementations.
 */
public class StrategyFactory {
    
    /**
     * Create a TypeMatcher from a strategy configuration
     * 
     * @param strategyConfig The strategy configuration to convert
     * @return Configured TypeMatcher instance
     * @throws IllegalArgumentException If strategy type is unsupported or required config is missing
     */
    public static TypeMatcher createMatcher(StrategyConfig strategyConfig) {
        switch (strategyConfig.getType()) {
            case ANNOTATION:
                return createAnnotationMatcher(strategyConfig);
                
            case REGEX:
                return createRegexMatcher(strategyConfig);
                
            case NAME_SUFFIX:
                return createNameSuffixMatcher(strategyConfig);
                
            case CUSTOM_ANNOTATION:
                return createCustomAnnotationMatcher(strategyConfig);
                
            default:
                throw new IllegalArgumentException("Unsupported strategy type: " + strategyConfig.getType());
        }
    }
    
    /**
     * Create an AnnotationTypeMatcher from configuration
     */
    private static TypeMatcher createAnnotationMatcher(StrategyConfig config) {
        String annotationType = config.getConfigString("annotationType");
        if (annotationType == null) {
            throw new IllegalArgumentException("annotationType is required for ANNOTATION strategy");
        }
        return new AnnotationTypeMatcher(annotationType);
    }
    
    /**
     * Create a RegexTypeMatcher from configuration
     */
    private static TypeMatcher createRegexMatcher(StrategyConfig config) {
        String pattern = config.getConfigString("pattern");
        if (pattern == null) {
            throw new IllegalArgumentException("pattern is required for REGEX strategy");
        }
        return new RegexTypeMatcher(pattern);
    }
    
    /**
     * Create a NameSuffixTypeMatcher from configuration
     */
    private static TypeMatcher createNameSuffixMatcher(StrategyConfig config) {
        String suffix = config.getConfigString("suffix");
        if (suffix == null) {
            throw new IllegalArgumentException("suffix is required for NAME_SUFFIX strategy");
        }
        return new NameSuffixTypeMatcher(suffix);
    }
    
    /**
     * Create a custom NewComponentStrategy from configuration
     */
    private static TypeMatcher createCustomAnnotationMatcher(StrategyConfig config) {
        String annotationType = config.getConfigString("annotationType");
        String propertyName = config.getConfigString("propertyName");
        String annotationProperty = config.getConfigString("annotationProperty");
        
        if (annotationType == null) {
            throw new IllegalArgumentException("annotationType is required for CUSTOM_ANNOTATION strategy");
        }
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName is required for CUSTOM_ANNOTATION strategy");
        }
        if (annotationProperty == null) {
            throw new IllegalArgumentException("annotationProperty is required for CUSTOM_ANNOTATION strategy");
        }
        
        return new NewComponentStratgy(annotationType, propertyName, annotationProperty);
    }
    
    /**
     * Validate that a strategy configuration has all required parameters
     * 
     * @param config The strategy configuration to validate
     * @throws IllegalArgumentException If required configuration is missing
     */
    public static void validateStrategyConfig(StrategyConfig config) {
        if (config.getType() == null) {
            throw new IllegalArgumentException("Strategy type is required");
        }
        
        if (config.getContainerMapping() == null) {
            throw new IllegalArgumentException("Container mapping is required");
        }
        
        // Validate type-specific requirements
        switch (config.getType()) {
            case ANNOTATION:
                if (config.getConfigString("annotationType") == null) {
                    throw new IllegalArgumentException("annotationType is required for ANNOTATION strategy");
                }
                break;
                
            case REGEX:
                if (config.getConfigString("pattern") == null) {
                    throw new IllegalArgumentException("pattern is required for REGEX strategy");
                }
                break;
                
            case NAME_SUFFIX:
                if (config.getConfigString("suffix") == null) {
                    throw new IllegalArgumentException("suffix is required for NAME_SUFFIX strategy");
                }
                break;
                
            case CUSTOM_ANNOTATION:
                if (config.getConfigString("annotationType") == null ||
                    config.getConfigString("propertyName") == null ||
                    config.getConfigString("annotationProperty") == null) {
                    throw new IllegalArgumentException("annotationType, propertyName, and annotationProperty are required for CUSTOM_ANNOTATION strategy");
                }
                break;
        }
    }
}
