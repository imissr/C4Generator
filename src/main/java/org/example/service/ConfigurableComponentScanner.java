package org.example.service;

import com.structurizr.component.ComponentFinder;
import com.structurizr.component.ComponentFinderBuilder;
import com.structurizr.component.ComponentFinderStrategyBuilder;
import com.structurizr.component.matcher.TypeMatcher;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import org.example.c4.C4ModelGenerator;
import org.example.utils.StrategyFactory;
import org.example.config.StrategyConfig;
import org.example.config.StrategyConfiguration;
import org.example.model.ComponentDetail;

import java.io.File;
import java.util.List;
import java.util.Map;


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
            C4ModelGenerator.assignRealtionFromJson(container, componentMap);
        }
    }
    
 void applyStrategy(Container container, File scanPath, StrategyConfig strategyConfig) {
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
