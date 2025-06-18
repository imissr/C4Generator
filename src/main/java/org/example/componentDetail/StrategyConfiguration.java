package org.example.componentDetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Top-level configuration class for strategy-based component discovery.
 * 
 * This class loads and manages the complete strategy configuration from JSON,
 * including individual strategy definitions and global configuration settings.
 * It serves as the entry point for configuring how the C4 model generator
 * discovers components across different containers.
 */
@Getter
@Setter
public class StrategyConfiguration {
    
    /**
     * List of all component discovery strategies to apply
     */
    @JsonProperty("strategies")
    private List<StrategyConfig> strategies;
    
    /**
     * Global configuration settings that apply to all strategies
     */
    @JsonProperty("globalConfig")
    private GlobalConfig globalConfig;
    
    /**
     * Load strategy configuration from a JSON file
     * 
     * @param configFile The JSON configuration file to load
     * @return Parsed strategy configuration
     * @throws IOException If file reading or JSON parsing fails
     */
    public static StrategyConfiguration loadFromFile(File configFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(configFile, StrategyConfiguration.class);
    }
    
    /**
     * Get strategies filtered by container mapping
     * 
     * @param containerName The container name to filter by
     * @return List of strategies that target the specified container
     */
    public List<StrategyConfig> getStrategiesForContainer(String containerName) {
        return strategies.stream()
                .filter(strategy -> containerName.equals(strategy.getContainerMapping()))
                .filter(StrategyConfig::isEnabled)
                .toList();
    }
    
    /**
     * Global configuration settings
     */
    @Getter
    @Setter
    public static class GlobalConfig {
        
        /**
         * Whether to exclude inner/anonymous classes from discovery
         */
        @JsonProperty("excludeInnerClasses")
        private boolean excludeInnerClasses = true;
        
        /**
         * Whether to exclude test classes from discovery
         */
        @JsonProperty("excludeTestClasses")
        private boolean excludeTestClasses = true;
        
        /**
         * Base paths for scanning different containers
         */
        @JsonProperty("basePaths")
        private Map<String, String> basePaths;
        
        /**
         * Default technology tags for containers
         */
        @JsonProperty("defaultTechnologies")
        private Map<String, String> defaultTechnologies;
        
        /**
         * Custom component processors for post-discovery processing
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
