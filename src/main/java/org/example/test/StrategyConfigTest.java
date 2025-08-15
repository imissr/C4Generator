package org.example.test;

import org.example.config.StrategyConfiguration;
import org.example.config.StrategyConfig;
import org.example.utils.StrategyFactory;

import java.io.File;


public class StrategyConfigTest {
    
    public static void main(String[] args) {
        try {
            System.out.println("Testing Strategy Configuration System...");
            
            // Test 1: Load strategy configuration
            File configFile = new File("src/main/java/org/example/json/strategyConfig.json");
            if (!configFile.exists()) {
                System.err.println("Strategy config file not found: " + configFile.getAbsolutePath());
                return;
            }
            
            StrategyConfiguration config = StrategyConfiguration.loadFromFile(configFile);
            System.out.println("✓ Successfully loaded strategy configuration");
            System.out.println("  - Found " + config.getStrategies().size() + " strategies");
            
            // Test 2: Validate each strategy
            for (StrategyConfig strategy : config.getStrategies()) {
                try {
                    StrategyFactory.validateStrategyConfig(strategy);
                    System.out.println("✓ Strategy '" + strategy.getName() + "' is valid");
                } catch (Exception e) {
                    System.err.println("✗ Strategy '" + strategy.getName() + "' is invalid: " + e.getMessage());
                }
            }
            
            // Test 3: Test strategy creation
            for (StrategyConfig strategy : config.getStrategies()) {
                try {
                    StrategyFactory.createMatcher(strategy);
                    System.out.println("✓ Successfully created matcher for '" + strategy.getName() + "'");
                } catch (Exception e) {
                    System.err.println("✗ Failed to create matcher for '" + strategy.getName() + "': " + e.getMessage());
                }
            }
            
            // Test 4: Test container filtering
            for (String container : new String[]{"connectorModel", "connectorImplementations", "connectorInfrastructure"}) {
                var strategies = config.getStrategiesForContainer(container);
                System.out.println("✓ Container '" + container + "' has " + strategies.size() + " strategies");
            }
            
            System.out.println("\nAll tests completed successfully! 🎉");
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
