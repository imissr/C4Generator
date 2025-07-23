package org.example.utils;

import com.structurizr.component.matcher.AnnotationTypeMatcher;
import com.structurizr.component.matcher.NameSuffixTypeMatcher;
import com.structurizr.component.matcher.RegexTypeMatcher;
import com.structurizr.component.matcher.TypeMatcher;
import org.example.strategies.NewComponentStrategy;
import org.example.config.StrategyConfig;

/**
 * Factory class for creating TypeMatcher instances from strategy configurations.
 * 
 * <p>This factory serves as the bridge between JSON-based strategy configurations
 * and executable TypeMatcher implementations used by Structurizr's ComponentFinder.
 * It encapsulates the complexity of strategy instantiation and provides validation
 * for configuration parameters.</p>
 * 
 * <p>The factory supports all major component discovery patterns:</p>
 * <ul>
 *   <li><strong>ANNOTATION</strong> - Creates {@link AnnotationTypeMatcher} for annotation-based discovery</li>
 *   <li><strong>REGEX</strong> - Creates {@link RegexTypeMatcher} for pattern-based discovery</li>
 *   <li><strong>NAME_SUFFIX</strong> - Creates {@link NameSuffixTypeMatcher} for convention-based discovery</li>
 *   <li><strong>CUSTOM_ANNOTATION</strong> - Creates custom {@link NewComponentStratgy} for advanced annotation logic</li>
 * </ul>
 * 
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Validate strategy configurations before creating matchers</li>
 *   <li>Handle strategy-specific parameter extraction and validation</li>
 *   <li>Provide clear error messages for misconfigured strategies</li>
 *   <li>Maintain type safety between configurations and implementations</li>
 * </ul>
 * 
 * <p>Usage example:</p>
 * <pre>
 * StrategyConfig config = // loaded from JSON
 * TypeMatcher matcher = StrategyFactory.createMatcher(config);
 * ComponentFinder finder = new ComponentFinderBuilder()
 *     .forContainer(container)
 *     .withStrategy(new ComponentFinderStrategyBuilder()
 *         .matchedBy(matcher)
 *         .build())
 *     .build();
 * </pre>
 * 
 * @see StrategyConfig for configuration structure
 * @see TypeMatcher for the Structurizr component matching interface
 * @see NewComponentStratgy for custom OSGi annotation matching
 * 
 * @author C4 Model Generator
 * @version 1.0
 * @since 2025-06-26
 */
public class StrategyFactory {
    
    /**
     * Creates a TypeMatcher from a strategy configuration.
     * 
     * <p>This is the main factory method that analyzes the strategy type and delegates
     * to the appropriate specialized creation method. Each strategy type requires
     * different configuration parameters and produces different TypeMatcher implementations.</p>
     * 
     * <p>The method validates the strategy configuration before attempting to create
     * the matcher, ensuring that all required parameters are present and properly formatted.</p>
     * 
     * <p>Supported strategy types and their required configurations:</p>
     * <ul>
     *   <li><strong>ANNOTATION</strong>: requires "annotationType" parameter</li>
     *   <li><strong>REGEX</strong>: requires "pattern" parameter</li>
     *   <li><strong>NAME_SUFFIX</strong>: requires "suffix" parameter</li>
     *   <li><strong>CUSTOM_ANNOTATION</strong>: requires "annotationType", "propertyName", and "annotationProperty"</li>
     * </ul>
     * 
     * @param strategyConfig The strategy configuration containing type and parameters. Must not be null.
     * @return Configured TypeMatcher instance ready for use with ComponentFinder
     * @throws IllegalArgumentException If strategy type is unsupported, required configuration is missing, or parameters are invalid
     * 
     * @see #validateStrategyConfig(StrategyConfig) for validation details
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
     * Creates an AnnotationTypeMatcher from strategy configuration.
     * 
     * <p>Extracts the "annotationType" parameter and creates a Structurizr
     * AnnotationTypeMatcher that identifies classes marked with the specified annotation.
     * The annotation type should be provided as a fully qualified class name.</p>
     * 
     * @param config Strategy configuration containing the "annotationType" parameter
     * @return AnnotationTypeMatcher configured for the specified annotation
     * @throws IllegalArgumentException if "annotationType" parameter is missing or null
     */
    private static TypeMatcher createAnnotationMatcher(StrategyConfig config) {
        String annotationType = config.getConfigString("annotationType");
        if (annotationType == null) {
            throw new IllegalArgumentException("annotationType is required for ANNOTATION strategy");
        }
        return new AnnotationTypeMatcher(annotationType);
    }
    
    /**
     * Creates a RegexTypeMatcher from strategy configuration.
     * 
     * <p>Extracts the "pattern" parameter and creates a Structurizr RegexTypeMatcher
     * that identifies classes whose fully qualified names match the specified regular expression.
     * The pattern is applied to the complete canonical class name.</p>
     * 
     * @param config Strategy configuration containing the "pattern" parameter
     * @return RegexTypeMatcher configured with the specified regex pattern
     * @throws IllegalArgumentException if "pattern" parameter is missing or null
     */
    private static TypeMatcher createRegexMatcher(StrategyConfig config) {
        String pattern = config.getConfigString("pattern");
        if (pattern == null) {
            throw new IllegalArgumentException("pattern is required for REGEX strategy");
        }
        return new RegexTypeMatcher(pattern);
    }
    
    /**
     * Creates a NameSuffixTypeMatcher from strategy configuration.
     * 
     * <p>Extracts the "suffix" parameter and creates a Structurizr NameSuffixTypeMatcher
     * that identifies classes whose simple names end with the specified suffix.
     * This is useful for convention-based discovery patterns.</p>
     * 
     * @param config Strategy configuration containing the "suffix" parameter
     * @return NameSuffixTypeMatcher configured with the specified suffix
     * @throws IllegalArgumentException if "suffix" parameter is missing or null
     */
    private static TypeMatcher createNameSuffixMatcher(StrategyConfig config) {
        String suffix = config.getConfigString("suffix");
        if (suffix == null) {
            throw new IllegalArgumentException("suffix is required for NAME_SUFFIX strategy");
        }
        return new NameSuffixTypeMatcher(suffix);
    }
    
    /**
     * Creates a custom NewComponentStrategy from strategy configuration.
     * 
     * <p>This method creates the advanced custom annotation matcher that can examine
     * annotation properties and array elements. It's specifically designed for OSGi
     * components that use property arrays in their @Component annotations.</p>
     * 
     * <p>Required configuration parameters:</p>
     * <ul>
     *   <li><strong>annotationType</strong>: The annotation class to examine (e.g., "org.osgi.service.component.annotations.Component")</li>
     *   <li><strong>propertyName</strong>: The property name to search for (e.g., "connector")</li>
     *   <li><strong>annotationProperty</strong>: The annotation attribute containing the array (e.g., "property")</li>
     * </ul>
     * 
     * @param config Strategy configuration with annotation matching parameters
     * @return NewComponentStrategy instance configured for custom annotation property matching
     * @throws IllegalArgumentException if any required parameter is missing or null
     * 
     * @see NewComponentStrategy for implementation details
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
        
        return new NewComponentStrategy(annotationType, propertyName, annotationProperty);
    }
    
    /**
     * Validates that a strategy configuration has all required parameters.
     * 
     * <p>This method performs comprehensive validation of strategy configurations
     * before they are used to create TypeMatcher instances. It checks both
     * common requirements (type, container mapping) and strategy-specific
     * parameter requirements.</p>
     * 
     * <p>Validation rules:</p>
     * <ul>
     *   <li><strong>Common</strong>: strategy type and container mapping must be present</li>
     *   <li><strong>ANNOTATION</strong>: "annotationType" parameter required</li>
     *   <li><strong>REGEX</strong>: "pattern" parameter required</li>
     *   <li><strong>NAME_SUFFIX</strong>: "suffix" parameter required</li>
     *   <li><strong>CUSTOM_ANNOTATION</strong>: "annotationType", "propertyName", and "annotationProperty" required</li>
     * </ul>
     * 
     * <p>This validation helps catch configuration errors early and provides
     * clear error messages for debugging.</p>
     * 
     * @param config The strategy configuration to validate. Must not be null.
     * @throws IllegalArgumentException If any required configuration is missing or invalid,
     *         with a descriptive message indicating which parameter is problematic
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
