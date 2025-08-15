package org.example.c4;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.structurizr.model.*;
import org.example.model.ContainerConfigDetail;
import org.example.config.StrategyConfiguration;
import org.example.model.C4ModelConfigDetail;
import org.example.model.ComponentDetail;
import org.example.model.ContainerDetail;
import org.example.model.SoftwareSystemDetail;
import org.example.service.ComponentChangeDetector;
import org.example.service.ComponentSerializationService;
import org.example.service.ConfigurableComponentScanner;

/**
 * CI/CD-focused entry point for component change detection and model generation.
 *
 * <p>This class provides specialized functionality for continuous integration environments,
 * focusing on efficient change detection and automated documentation updates. It supports
 * different operational modes suitable for various CI/CD scenarios.</p>
 *
 * <p>Supported modes:</p>
 * <ul>
 *   <li><strong>change-detect</strong> - Only detect changes, exit with code 1 if changes found</li>
 *   <li><strong>generate-if-changed</strong> - Generate full model only if changes detected</li>
 *   <li><strong>serialize-only</strong> - Only serialize components without full model generation</li>
 *   <li><strong>baseline</strong> - Create initial baseline snapshot</li>
 * </ul>
 *
 * <p>This approach optimizes CI/CD pipeline execution time by avoiding unnecessary
 * full model generation when no architectural changes have occurred.</p>
 *
 * @author C4 Model Generator Team
 * @version 1.0
 * @since 2025-07-11
 */
public class C4ModelGeneratorCI {

    /**
     * CI/CD entry point with mode-based operation.
     *
     * @param args Command line arguments:
     *             [0] mode: "change-detect", "generate-if-changed", "serialize-only", "baseline"
     *             [1] optional: configuration file path (defaults to standard location)
     */
    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "change-detect";
        String configPath = args.length > 1 ? args[1] : "src/main/java/org/example/json/c4ModelConfig.json";

        System.out.println("=== C4 Model Generator - CI/CD Mode ===");
        System.out.println("Mode: " + mode);
        System.out.println("Config: " + configPath);

        try {
            switch (mode.toLowerCase()) {
                case "change-detect":
                    runChangeDetectionMode(configPath);
                    break;
                case "generate-if-changed":
                    runGenerateIfChangedMode(configPath);
                    break;
                case "serialize-only":
                    runSerializeOnlyMode(configPath);
                    break;
                case "baseline":
                    runBaselineMode(configPath);
                    break;
                default:
                    System.err.println("Unknown mode: " + mode);
                    printUsage();
                    System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("CI/CD execution failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Change detection mode - only detects changes and exits with appropriate code.
     * Exit code 0: No changes, Exit code 1: Changes detected, Exit code 2: Error
     */
    private static void runChangeDetectionMode(String configPath) throws Exception {
        System.out.println("\n=== CHANGE DETECTION MODE ===");

        // Quick component scan without full model generation
        Map<String, Container> containers = performQuickComponentScan(configPath);

        if (ComponentChangeDetector.validateContainersHaveComponents(containers)) {
            ComponentSerializationService.ComponentComparisonResult result =
                    ComponentChangeDetector.detectChanges(containers);

            if (result.hasChanges) {
                System.out.println("EXIT: Changes detected - triggering downstream pipeline");
                System.exit(1); // Signal changes to CI/CD
            } else {
                System.out.println("EXIT: No changes detected - pipeline can skip");
                System.exit(0); // No changes
            }
        } else {
            System.err.println("EXIT: Component validation failed");
            System.exit(2); // Error
        }
    }

    /**
     * Generate if changed mode - full generation only if changes detected.
     */
    private static void runGenerateIfChangedMode(String configPath) throws Exception {
        System.out.println("\n=== GENERATE IF CHANGED MODE ===");
        System.out.println("Changes detected - proceeding with full model generation");
        C4ModelGenerator.main(new String[]{});
        System.out.println("No changes detected - skipping full model generation");
    }




    /**
     * Serialize only mode - just serialize components without full model generation.
     */
    private static void runSerializeOnlyMode(String configPath) throws Exception {
        System.out.println("\n=== SERIALIZE ONLY MODE ===");

        Map<String, Container> containers = performQuickComponentScan(configPath);

        if (ComponentChangeDetector.validateContainersHaveComponents(containers)) {
            ComponentSerializationService.ComponentSnapshot snapshot =
                    ComponentSerializationService.serializeComponents(containers);
            ComponentSerializationService.saveSnapshotWithHistory(snapshot);
            System.out.println(" Component serialization completed");
        } else {
            System.err.println("Component validation failed");
            System.exit(1);
        }
    }

    /**
     * Baseline mode - create initial baseline snapshot.
     */
    private static void runBaselineMode(String configPath) throws Exception {
        System.out.println("\n=== BASELINE MODE ===");

        Map<String, Container> containers = performQuickComponentScan(configPath);

        if (ComponentChangeDetector.validateContainersHaveComponents(containers)) {
            ComponentChangeDetector.createBaselineSnapshot(containers);
            System.out.println(" Baseline snapshot created");
        } else {
            System.err.println("Component validation failed");
            System.exit(1);
        }
    }

    /**
     * Performs a quick component scan without full model generation.
     * This is optimized for CI/CD scenarios where we only need component information.
     */
    private static Map<String, Container> performQuickComponentScan(String configPath) throws Exception {
        System.out.println("Performing quick component scan...");

        // Load configuration
        File c4ConfigJson = new File(configPath);
        C4ModelConfigDetail c4Config = C4ModelConfigDetail.loadFromFile(c4ConfigJson, true);

        // Create minimal model structure for scanning
        com.structurizr.Workspace workspace = new com.structurizr.Workspace("CI-Scan", "Quick scan for CI/CD");
        Model model = workspace.getModel();

        // Create software systems and containers (minimal setup)
        Map<String, SoftwareSystem> softwareSystems = new HashMap<>();
        for (SoftwareSystemDetail systemConfig : c4Config.getSoftwareSystems()) {
            SoftwareSystem system = model.addSoftwareSystem(systemConfig.getName(), systemConfig.getDescription());
            softwareSystems.put(systemConfig.getName(), system);
        }

        Map<String, Container> containers = new HashMap<>();
        for (ContainerConfigDetail containerConfig : c4Config.getContainers()) {
            String systemName = containerConfig.getSoftwareSystemName();
            SoftwareSystem parentSystem = softwareSystems.get(systemName);

            if (parentSystem == null) {
                parentSystem = softwareSystems.values().iterator().next();
            }

            Container container = parentSystem.addContainer(
                    containerConfig.getName(),
                    containerConfig.getDescription(),
                    containerConfig.getTechnology()
            );
            containers.put(containerConfig.getName(), container);
        }

        // Load strategy configuration and scan components
        File strategyConfigJson = new File("src/main/java/org/example/json/strategyConfig.json");
        StrategyConfiguration strategyConfig = StrategyConfiguration.loadFromFile(strategyConfigJson);
        ConfigurableComponentScanner scanner = new ConfigurableComponentScanner(strategyConfig);

        // Build component maps (same as main generator)
        Map<String, ContainerDetail> allContainers = c4Config.getContainerMap();
        Map<String, Map<String, ComponentDetail>> containerComponentMaps = new HashMap<>();

        for (Map.Entry<String, ContainerDetail> entry : allContainers.entrySet()) {
            String containerKey = entry.getKey();
            ContainerDetail containerDetail = entry.getValue();

            if (containerDetail != null) {
                Map<String, ComponentDetail> componentMap = containerDetail.getComponentMap();
                containerComponentMaps.put(containerKey, componentMap);
                System.out.println(" " + containerKey + " component config loaded: " +
                        (componentMap != null ? componentMap.size() : 0) + " components");
            }
        }

        // Get containers for scanning (same approach as main generator)
        Map<String, Container> containersForScanning = new HashMap<>();
        for (ContainerConfigDetail containerConfig : c4Config.getContainers()) {
            Container container = containers.get(containerConfig.getName());
            if (container != null) {
                containersForScanning.put(containerConfig.getName(), container);
                System.out.println("- " + containerConfig.getName() + ": ");
            } else {
                System.out.println("- " + containerConfig.getName() + ":  (not found)");
            }
        }

        for (Map.Entry<String, Container> entry : containersForScanning.entrySet()) {
            String containerName = entry.getKey();
            Container container = entry.getValue();

            // Find corresponding container key in the component configuration
            String containerKey = findContainerKeyInConfig(containerName, allContainers);
            Map<String, ComponentDetail> componentMap = containerComponentMaps.get(containerKey);

            System.out.println("Scanning container: " + container.getName() +
                    " (config key: " + containerKey + ")");

            scanner.scanContainer(container, containerKey, componentMap);
            System.out.println(" Completed scanning: " + container.getName() +
                    " (" + container.getComponents().size() + " components)");
        }

        System.out.println(" Quick component scan completed");
        return containersForScanning;
    }

    /**
     * Helper method to find container key in configuration.
     * Uses multiple fallback strategies to map container names to configuration keys.
     */
    private static String findContainerKeyInConfig(String containerName, Map<String, ContainerDetail> allContainers) {
        if (containerName == null || allContainers == null || allContainers.isEmpty()) {
            return null;
        }

        String normalizedContainerName = containerName.toLowerCase().trim();

        // Strategy 1: Exact match (case-insensitive)
        for (String key : allContainers.keySet()) {
            if (key.toLowerCase().trim().equals(normalizedContainerName)) {
                System.out.println(" Found exact match: " + containerName + " -> " + key);
                return key;
            }
        }

        // Strategy 2: Exact match without spaces and special characters
        String cleanContainerName = normalizedContainerName.replaceAll("[\\s\\-_]", "");
        for (String key : allContainers.keySet()) {
            String cleanKey = key.toLowerCase().replaceAll("[\\s\\-_]", "");
            if (cleanKey.equals(cleanContainerName)) {
                System.out.println(" Found normalized match: " + containerName + " -> " + key);
                return key;
            }
        }

        // Strategy 3: Key contains container name (without spaces)
        for (String key : allContainers.keySet()) {
            String cleanKey = key.toLowerCase().replaceAll("[\\s\\-_]", "");
            if (cleanKey.contains(cleanContainerName)) {
                System.out.println(" Found key containing container name: " + containerName + " -> " + key);
                return key;
            }
        }

        // Strategy 4: Container name contains key (without spaces)
        for (String key : allContainers.keySet()) {
            String cleanKey = key.toLowerCase().replaceAll("[\\s\\-_]", "");
            if (cleanContainerName.contains(cleanKey)) {
                System.out.println(" Found container name containing key: " + containerName + " -> " + key);
                return key;
            }
        }

        // Strategy 5: Partial word matching (split by spaces/dashes/underscores)
        String[] containerWords = normalizedContainerName.split("[\\s\\-_]+");
        for (String key : allContainers.keySet()) {
            String[] keyWords = key.toLowerCase().split("[\\s\\-_]+");

            // Check if any container word matches any key word
            for (String containerWord : containerWords) {
                for (String keyWord : keyWords) {
                    if (containerWord.equals(keyWord) && containerWord.length() > 2) { // Avoid matching very short words
                        System.out.println(" Found word match: " + containerName + " -> " + key + " (matched word: " + containerWord + ")");
                        return key;
                    }
                }
            }
        }

        // Strategy 6: Fuzzy matching - check if key is a substring of container name or vice versa
        for (String key : allContainers.keySet()) {
            String cleanKey = key.toLowerCase().replaceAll("[\\s\\-_]", "");
            if (cleanKey.length() > 3 && (cleanContainerName.contains(cleanKey) || cleanKey.contains(cleanContainerName))) {
                System.out.println(" Found fuzzy match: " + containerName + " -> " + key);
                return key;
            }
        }

        System.out.println(" Warning: No configuration key found for container: " + containerName);
        System.out.println("Available keys: " + allContainers.keySet());
        return null;
    }

    /**
     * Prints usage information for the CI/CD runner.
     */
    private static void printUsage() {
        System.out.println("\nUsage: java C4ModelGeneratorCI <mode> [config-path]");
        System.out.println("\nModes:");
        System.out.println("  change-detect      - Detect changes and exit with code 1 if found");
        System.out.println("  generate-if-changed - Full generation only if changes detected");
        System.out.println("  serialize-only     - Only serialize components");
        System.out.println("  baseline          - Create initial baseline snapshot");
        System.out.println("\nConfig path defaults to: src/main/java/org/example/json/c4ModelConfig.json");
    }
}
