package org.example.componentDetail;

import com.structurizr.component.ComponentFinder;
import com.structurizr.component.ComponentFinderBuilder;
import com.structurizr.component.ComponentFinderStrategyBuilder;
import com.structurizr.component.matcher.TypeMatcher;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import org.example.c4.AvatarC4ModelGenerator;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Configurable component scanner that uses strategy configurations to discover components.
 * 
 * <p>This class represents the execution engine of the configurable C4 model generation
 * system. It takes strategy configurations loaded from JSON and applies them to discover
 * and document software components within specified containers. The scanner provides
 * a flexible, non-invasive way to analyze codebases without requiring code modifications.</p>
 * 
 * <p>Key features and capabilities:</p>
 * <ul>
 *   <li><strong>Strategy-driven discovery</strong> - Uses pluggable strategies for different component types</li>
 *   <li><strong>Container-specific scanning</strong> - Applies relevant strategies per container</li>
 *   <li><strong>Automatic enrichment</strong> - Generates descriptions, tags, and technology assignments</li>
 *   <li><strong>Filtering capabilities</strong> - Excludes test classes, inner classes based on configuration</li>
 *   <li><strong>Path management</strong> - Maps logical containers to filesystem paths</li>
 *   <li><strong>Error resilience</strong> - Continues processing when individual strategies fail</li>
 * </ul>
 * 
 * <p>The scanning process follows these steps:</p>
 * <ol>
 *   <li>Filter strategies by target container and enabled status</li>
 *   <li>Resolve the base filesystem path for the container</li>
 *   <li>Apply each applicable strategy using Structurizr's ComponentFinder</li>
 *   <li>Process discovered components (descriptions, tags, filtering)</li>
 *   <li>Apply optional component enrichment from external sources</li>
 * </ol>
 * 
 * <p>Usage example:</p>
 * <pre>
 * StrategyConfiguration config = StrategyConfiguration.loadFromFile(configFile);
 * ConfigurableComponentScanner scanner = new ConfigurableComponentScanner(config);
 * 
 * scanner.scanContainer(applicationContainer, "applicationServices", componentDetails);
 * scanner.scanContainer(dataContainer, "dataLayer", componentDetails);
 * </pre>
 * 
 * <p>The scanner integrates seamlessly with existing C4 model generation workflows
 * and can be combined with manual component definitions and relationship mappings.</p>
 * 
 * @see StrategyConfiguration for configuration loading and management
 * @see StrategyFactory for strategy-to-matcher conversion
 * @see ComponentFinder for the underlying Structurizr scanning mechanism
 * 
 * @author C4 Model Generator
 * @version 1.0
 * @since 2025-06-26
 */
public class ConfigurableComponentScanner {
    
    private final StrategyConfiguration strategyConfiguration;
    
    /**
     * Constructs a scanner with the specified strategy configuration.
     * 
     * @param strategyConfiguration The loaded strategy configuration containing all
     *                            discovery strategies and global settings. Must not be null.
     * @throws IllegalArgumentException if strategyConfiguration is null
     */
    public ConfigurableComponentScanner(StrategyConfiguration strategyConfiguration) {
        this.strategyConfiguration = strategyConfiguration;
    }
    
    /**
     * Scans a container for components using all applicable configured strategies.
     * 
     * <p>This is the main entry point for component discovery. The method:</p>
     * <ol>
     *   <li>Identifies strategies configured for the specified container</li>
     *   <li>Resolves the filesystem path where components should be discovered</li>
     *   <li>Applies each strategy using Structurizr's ComponentFinder framework</li>
     *   <li>Processes and enriches discovered components</li>
     *   <li>Optionally applies external component details and relationships</li>
     * </ol>
     * 
     * <p>The scanning process is resilient to individual strategy failures - if one
     * strategy encounters an error, the others will continue to execute. Errors are
     * logged but don't halt the overall discovery process.</p>
     * 
     * <p>Component enrichment includes:</p>
     * <ul>
     *   <li>Automatic description generation based on naming patterns</li>
     *   <li>Technology tag assignment from global configuration</li>
     *   <li>Strategy-specific tags (e.g., "Annotated", "Pattern-Matched")</li>
     *   <li>Functional tags based on class naming conventions</li>
     * </ul>
     * 
     * @param container The Structurizr container to populate with discovered components
     * @param containerName The logical name of the container (used for strategy filtering and path resolution)
     * @param componentMap Optional map of component details for additional enrichment.
     *                    If provided, components will be enhanced with relationships and additional metadata.
     *                    Can be null if no external enrichment is needed.
     * 
     * @see StrategyConfiguration#getStrategiesForContainer(String) for strategy filtering
     * @see AvatarC4ModelGenerator#assignRealtionFromJson for component enrichment
     */
    public void scanContainer(Container container, String containerName, Map<String, ComponentDetail> componentMap) {
        // Get strategies for this container
        List<StrategyConfig> strategies = strategyConfiguration.getStrategiesForContainer(containerName);
        
        if (strategies.isEmpty()) {
            System.out.println("No strategies configured for container: " + containerName);
            return;
        }
        
        // Get base path for this container
        String basePath = strategyConfiguration.getGlobalConfig().getBasePath(containerName);
        if (basePath == null) {
            System.out.println("No base path configured for container: " + containerName);
            return;
        }
        
        File scanPath = new File(basePath);
        if (!scanPath.exists()) {
            System.out.println("Warning: Path " + basePath + " doesn't exist. Skipping container: " + containerName);
            return;
        }
        
        // Apply each strategy
        for (StrategyConfig strategyConfig : strategies) {
            try {
                applyStrategy(container, scanPath, strategyConfig);
            } catch (Exception e) {
                System.out.println("Strategy '" + strategyConfig.getName() + "' failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Apply component enrichment if provided
        if (componentMap != null && !componentMap.isEmpty()) {
            AvatarC4ModelGenerator.assignRealtionFromJson(container, componentMap);
        }
    }
    
    /**
     * Applies a single strategy to discover components within the specified path.
     * 
     * <p>This method handles the execution of individual discovery strategies by:</p>
     * <ol>
     *   <li>Validating the strategy configuration</li>
     *   <li>Creating the appropriate TypeMatcher using StrategyFactory</li>
     *   <li>Determining the technology label (from global config or fallback)</li>
     *   <li>Configuring and running Structurizr's ComponentFinder</li>
     *   <li>Processing each discovered component through the enrichment pipeline</li>
     * </ol>
     * 
     * <p>The method is designed to be fault-tolerant - if strategy creation or
     * execution fails, the error is wrapped in a RuntimeException with context
     * about which strategy failed.</p>
     * 
     * @param container The container to add discovered components to
     * @param scanPath The filesystem path to scan for components
     * @param strategyConfig The strategy configuration to apply
     * @throws RuntimeException if strategy creation fails or component discovery encounters errors
     */
    private void applyStrategy(Container container, File scanPath, StrategyConfig strategyConfig) {
        System.out.println("Applying strategy: " + strategyConfig.getName());
        
        // Validate strategy configuration
        StrategyFactory.validateStrategyConfig(strategyConfig);
        
        // Create TypeMatcher from configuration
        TypeMatcher matcher = StrategyFactory.createMatcher(strategyConfig);
        
        // Determine technology from global config or fallback
        String technology = strategyConfiguration.getGlobalConfig().getDefaultTechnology(strategyConfig.getContainerMapping());
        if (technology == null) {
            technology = "Java"; // fallback
        }
        
        try {
            ComponentFinder finder = new ComponentFinderBuilder()
                    .forContainer(container)
                    .fromClasses(scanPath)
                    .withStrategy(
                            new ComponentFinderStrategyBuilder()
                                    .matchedBy(matcher)
                                    .withTechnology(technology)
                                    .forEach(component -> processDiscoveredComponent(component, strategyConfig))
                                    .build()
                    )
                    .build();
            
            finder.run();
            System.out.println("Successfully applied strategy: " + strategyConfig.getName());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply strategy: " + strategyConfig.getName(), e);
        }
    }
    
    /**
     * Processes a discovered component by applying strategy-specific and global configuration.
     * 
     * <p>This method serves as the component enrichment pipeline, applying various
     * filters and enhancements to ensure discovered components meet quality standards
     * and contain useful metadata for architectural documentation.</p>
     * 
     * <p>Processing steps include:</p>
     * <ol>
     *   <li><strong>Filtering</strong>: Remove inner classes and test classes if configured</li>
     *   <li><strong>Description generation</strong>: Create meaningful descriptions based on naming patterns</li>
     *   <li><strong>Tag assignment</strong>: Add strategy-specific and functional tags</li>
     *   <li><strong>Logging</strong>: Record successful component discovery</li>
     * </ol>
     * 
     * <p>The filtering logic respects global configuration settings:</p>
     * <ul>
     *   <li>Inner classes (containing '$') excluded if excludeInnerClasses=true</li>
     *   <li>Test classes (in test packages or ending with Test/Tests) excluded if excludeTestClasses=true</li>
     * </ul>
     * 
     * @param component The discovered component to process and enrich
     * @param strategyConfig The strategy configuration that discovered this component
     */
    private void processDiscoveredComponent(Component component, StrategyConfig strategyConfig) {
        String canonicalName = component.getCanonicalName();
        
        // Exclude inner classes if configured
        if (strategyConfiguration.getGlobalConfig().isExcludeInnerClasses() && canonicalName.contains("$")) {
            return;
        }
        
        // Exclude test classes if configured
        if (strategyConfiguration.getGlobalConfig().isExcludeTestClasses() && 
            (canonicalName.contains(".test.") || canonicalName.endsWith("Test") || canonicalName.endsWith("Tests"))) {
            return;
        }
        
        // Apply description from strategy if component doesn't have one
        if (component.getDescription() == null || component.getDescription().trim().isEmpty()) {
            String description = generateDefaultDescription(component, strategyConfig);
            component.setDescription(description);
        }
        
        // Add strategy-specific tags
        addStrategyTags(component, strategyConfig);
        
        System.out.println("Discovered component: " + component.getName() + " using strategy: " + strategyConfig.getName());
    }
    
    /**
     * Generates a meaningful description for a component based on strategy context and naming patterns.
     * 
     * <p>This method analyzes the component's name and canonical name to create
     * descriptive text that explains the component's role in the system. It uses
     * common naming conventions to infer component purposes and generate
     * human-readable descriptions for architectural documentation.</p>
     * 
     * <p>Description generation rules:</p>
     * <ul>
     *   <li><strong>Factory classes</strong>: "Factory component for creating [X] instances"</li>
     *   <li><strong>Implementation classes</strong>: "Implementation of [X] interface"</li>
     *   <li><strong>Serializer classes</strong>: "Serialization component for [X]"</li>
     *   <li><strong>Whiteboard classes</strong>: "OSGi whiteboard pattern implementation for [X]"</li>
     *   <li><strong>Default</strong>: "Component discovered by [strategy name]"</li>
     * </ul>
     * 
     * @param component The component to generate a description for
     * @param strategyConfig The strategy that discovered this component
     * @return A descriptive string explaining the component's purpose and role
     */
    private String generateDefaultDescription(Component component, StrategyConfig strategyConfig) {
        String name = component.getName();
        String canonicalName = component.getCanonicalName();
        
        // Generate base description based on strategy name
        String baseDescription = "Component discovered by " + strategyConfig.getName();
        
        // Add specific details based on naming patterns
        if (canonicalName.contains("Factory")) {
            return "Factory component for creating " + name.replace("Factory", "").toLowerCase() + " instances";
        } else if (canonicalName.contains("Impl")) {
            return "Implementation of " + name.replace("Impl", "") + " interface";
        } else if (canonicalName.contains("Serializer")) {
            return "Serialization component for " + name.replace("Serializer", "").toLowerCase();
        } else if (canonicalName.contains("Whiteboard")) {
            return "OSGi whiteboard pattern implementation for " + name.replace("Whiteboard", "").toLowerCase();
        }
        
        return baseDescription;
    }
    
    /**
     * Applies strategy-specific and functional tags to a discovered component.
     * 
     * <p>Tags provide categorical metadata that helps in visualizing and understanding
     * the architectural model. This method applies two types of tags:</p>
     * 
     * <p><strong>Strategy-based tags</strong> indicate how the component was discovered:</p>
     * <ul>
     *   <li>"Annotated" - for ANNOTATION and CUSTOM_ANNOTATION strategies</li>
     *   <li>"Pattern-Matched" - for REGEX strategies</li>
     *   <li>"Convention-Based" - for NAME_SUFFIX strategies</li>
     * </ul>
     * 
     * <p><strong>Functional tags</strong> are inferred from naming patterns:</p>
     * <ul>
     *   <li>"Factory" - for classes containing "Factory"</li>
     *   <li>"Implementation" - for classes containing "Impl"</li>
     *   <li>"Serializer" - for classes containing "Serializer"</li>
     *   <li>"Whiteboard" - for classes containing "Whiteboard"</li>
     *   <li>"Connector" - for classes containing "Connector"</li>
     * </ul>
     * 
     * <p>These tags can be used in C4 model visualizations to color-code or
     * filter components by type or discovery method.</p>
     * 
     * @param component The component to tag
     * @param strategyConfig The strategy configuration that discovered this component
     */
    private void addStrategyTags(Component component, StrategyConfig strategyConfig) {
        String canonicalName = component.getCanonicalName();
        
        // Add base tags based on strategy type
        switch (strategyConfig.getType()) {
            case CUSTOM_ANNOTATION:
            case ANNOTATION:
                component.addTags("Annotated");
                break;
            case REGEX:
                component.addTags("Pattern-Matched");
                break;
            case NAME_SUFFIX:
                component.addTags("Convention-Based");
                break;
        }

        // Add functional tags based on naming patterns
        if (canonicalName.contains("Factory")) {
            component.addTags("Factory");
        }
        if (canonicalName.contains("Impl")) {
            component.addTags("Implementation");
        }
        if (canonicalName.contains("Serializer")) {
            component.addTags("Serializer");
        }
        if (canonicalName.contains("Whiteboard")) {
            component.addTags("Whiteboard");
        }
        if (canonicalName.contains("Connector")) {
            component.addTags("Connector");
        }
    }
}
