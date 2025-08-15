package org.example.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.example.service.ConfigurableComponentScanner;
import org.example.utils.StrategyFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@Getter
@Setter
public class StrategyConfiguration {
    
    /**
     * List of all component discovery strategies to apply
     */
    @JsonProperty("strategies")
    private List<StrategyConfig> strategies;
    
    /**
     * Global configuration settings that apply to all strategies.
     * 
     * Contains cross-cutting concerns like path management, filtering rules,
     * and default technology assignments that affect all strategy executions.
     */
    @JsonProperty("globalConfig")
    private GlobalConfig globalConfig;
    
    /**
     * Loads strategy configuration from a JSON file.
     * 
     * <p>This factory method deserializes a complete strategy configuration from
     * a JSON file using Jackson ObjectMapper. The JSON structure must conform to
     * the expected schema with "strategies" and "globalConfig" top-level properties.</p>
     * 
     * <p>The loaded configuration is validated during deserialization to ensure
     * all required fields are present and properly formatted.</p>
     * 
     * @param configFile The JSON configuration file to load. Must exist and be readable.
     * @return Fully parsed and validated strategy configuration ready for use
     * @throws IOException If file reading fails, file doesn't exist, or JSON parsing fails
     * @throws IllegalArgumentException If the JSON structure is invalid or missing required fields
     * 
     * @see #getStrategiesForContainer(String) for filtering strategies by container
     */
    public static StrategyConfiguration loadFromFile(File configFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(configFile, StrategyConfiguration.class);
    }
    
    /**
     * Filters strategies by container mapping and enabled status.
     * 
     * <p>Returns only the strategies that are specifically configured to target
     * the specified container and are currently enabled. This allows selective
     * application of discovery strategies per container, supporting fine-grained
     * control over component discovery.</p>
     * 
     * <p>The filtering process:</p>
     * <ol>
     *   <li>Matches strategies where containerMapping equals the specified container name</li>
     *   <li>Excludes disabled strategies (enabled=false)</li>
     *   <li>Returns an immutable list of applicable strategies</li>
     * </ol>
     * 
     * @param containerName The logical container name to filter by (e.g., "applicationServices", "dataLayer")
     * @return Immutable list of enabled strategies targeting the specified container. May be empty if no strategies match.
     * 
     * @see StrategyConfig#getContainerMapping()
     * @see StrategyConfig#isEnabled()
     */
    public List<StrategyConfig> getStrategiesForContainer(String containerName) {
        return strategies.stream()
                .filter(strategy -> containerName.equals(strategy.getContainerMapping()))
                .filter(StrategyConfig::isEnabled)
                .toList();
    }
    
    /**
     * Global configuration settings that affect all strategy executions.
     * 
     * <p>This nested class encapsulates system-wide settings that influence how
     * all strategies behave during component discovery. It provides centralized
     * control over filtering rules, path management, and default assignments.</p>
     * 
     * <p>Key responsibilities include:</p>
     * <ul>
     *   <li>Managing base paths for different container types</li>
     *   <li>Defining default technology assignments</li>
     *   <li>Controlling component filtering (inner classes, test classes)</li>
     *   <li>Providing hooks for custom component processors</li>
     * </ul>
     * 
     * @since 2025-06-26
     */
    @Getter
    @Setter
    public static class GlobalConfig {
        
        /**
         * Controls whether inner and anonymous classes are excluded from component discovery.
         * 
         * <p>When enabled, classes containing '$' in their canonical name (indicating
         * inner or anonymous classes) will be filtered out during component scanning.
         * This helps focus on primary application components rather than implementation details.</p>
         * 
         * @default true
         */
        @JsonProperty("excludeInnerClasses")
        private boolean excludeInnerClasses = true;
        
        /**
         * Controls whether test classes are excluded from component discovery.
         * 
         * <p>When enabled, classes matching test patterns will be filtered out:</p>
         * <ul>
         *   <li>Classes in packages containing ".test."</li>
         *   <li>Classes ending with "Test" or "Tests"</li>
         * </ul>
         * 
         * <p>This ensures that only production code components are included in
         * the architectural documentation.</p>
         * 
         * @default true
         */
        @JsonProperty("excludeTestClasses")
        private boolean excludeTestClasses = true;
        
        /**
         * Maps container names to their corresponding filesystem scan paths.
         * 
         * <p>Each container in the C4 model needs a base path where its components
         * should be discovered. This map provides that association, allowing different
         * containers to scan different parts of the project structure.</p>
         * 
         * <p>Example mapping:</p>
         * <pre>
         * {
         *   "applicationServices": "/project/src/main/java/com/example/services",
         *   "dataLayer": "/project/src/main/java/com/example/data",
         *   "webLayer": "/project/src/main/java/com/example/web"
         * }
         * </pre>
         */
        @JsonProperty("basePaths")
        private Map<String, String> basePaths;
        
        /**
         * Assigns default technology labels to containers when specific strategies don't provide them.
         * 
         * <p>This mapping ensures that discovered components receive appropriate technology
         * tags even when the discovery strategy doesn't specify one. Helps maintain
         * consistency in architectural documentation.</p>
         * 
         * <p>Example assignments:</p>
         * <pre>
         * {
         *   "applicationServices": "Spring Framework",
         *   "dataLayer": "JPA/Hibernate", 
         *   "webLayer": "Spring MVC"
         * }
         * </pre>
         */
        @JsonProperty("defaultTechnologies")
        private Map<String, String> defaultTechnologies;
        
        /**
         * Custom component processors for advanced post-discovery processing.
         * 
         * <p>Provides extensibility hooks for custom component enrichment, validation,
         * or transformation logic that should be applied after components are discovered
         * but before the final C4 model is generated.</p>
         * 
         * <p>This allows for project-specific customizations without modifying core code.</p>
         */
        @JsonProperty("componentProcessors")
        private Map<String, Object> componentProcessors;
        
        /**
         * Get base path for a container
         */
        public String getBasePath(String containerName) {
            return basePaths != null ? basePaths.get(containerName) : null;
        }
        
        /**
         * Get default technology for a container
         */
        public String getDefaultTechnology(String containerName) {
            return defaultTechnologies != null ? defaultTechnologies.get(containerName) : null;
        }
    }
}
