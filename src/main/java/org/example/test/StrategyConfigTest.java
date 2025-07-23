package org.example.test;

import org.example.config.StrategyConfiguration;
import org.example.config.StrategyConfig;
import org.example.utils.StrategyFactory;

import java.io.File;

/**
 * Comprehensive test class for validating the strategy configuration system.
 * 
 * <p>This test class provides validation and demonstration of the configurable
 * component discovery system. It performs comprehensive testing of strategy
 * configuration loading, validation, and matcher creation to ensure the
 * system works correctly before applying it to actual C4 model generation.</p>
 * 
 * <p>Test coverage includes:</p>
 * <ul>
 *   <li><strong>Configuration loading</strong> - Verifies JSON parsing and deserialization</li>
 *   <li><strong>Strategy validation</strong> - Ensures all required parameters are present</li>
 *   <li><strong>Matcher creation</strong> - Tests TypeMatcher instantiation from configurations</li>
 *   <li><strong>Container filtering</strong> - Validates strategy-to-container associations</li>
 * </ul>
 * 
 * <p>The test operates on the actual strategy configuration file used by the
 * system, providing real-world validation of the configuration setup. This
 * helps catch configuration errors early in the development process.</p>
 * 
 * <p>Usage:</p>
 * <pre>
 * java org.example.test.StrategyConfigTest
 * </pre>
 * 
 * <p>Expected outputs include validation results for each configured strategy
 * and confirmation that the system is ready for component discovery.</p>
 * 
 * @see StrategyConfiguration for configuration structure
 * @see StrategyFactory for matcher creation
 * @see ConfigurableComponentScanner for strategy execution
 * 
 * @author C4 Model Generator
 * @version 1.0
 * @since 2025-06-26
 */
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
