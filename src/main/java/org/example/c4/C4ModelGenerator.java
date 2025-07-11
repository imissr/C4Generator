package org.example.c4;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.structurizr.Workspace;
import com.structurizr.io.json.JsonWriter;
import com.structurizr.model.*;
import com.structurizr.model.Component;
import com.structurizr.view.*;
import org.example.componentDetail.*;

/**
 * Configurable C4 Model Generator - Advanced architectural documentation system.
 * 
 * <p>This sophisticated code analysis and documentation generator creates comprehensive
 * C4 architecture models from existing Java codebases using configurable discovery
 * strategies. The system combines static configuration with dynamic code analysis
 * to automatically generate accurate, up-to-date architectural documentation.</p>
 * 
 * <p><strong>Core Capabilities:</strong></p>
 * <ul>
 *   <li><strong>Multi-strategy component discovery</strong> - Uses configurable JSON-based strategies</li>
 *   <li><strong>Automated relationship mapping</strong> - Discovers and documents component interactions</li>
 *   <li><strong>Container-based organization</strong> - Organizes components into logical architectural layers</li>
 *   <li><strong>JSON schema validation</strong> - Ensures configuration integrity and quality</li>
 *   <li><strong>Flexible output formats</strong> - Compatible with Structurizr and other C4 tools</li>
 * </ul>
 * 
 * <p><strong>Architecture Documentation Generated:</strong></p>
 * <ul>
 *   <li><strong>System Context</strong> - Shows users, external systems, and system boundaries</li>
 *   <li><strong>Container Views</strong> - Displays main architectural containers and their relationships</li>
 *   <li><strong>Component Views</strong> - Details internal components within each container</li>
 *   <li><strong>Relationship Mapping</strong> - Documents dependencies and interactions between elements</li>
 * </ul>
 * 
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Project-agnostic design supporting any Java codebase</li>
 *   <li>Configurable component discovery without code modifications</li>
 *   <li>Automated container key mapping with fallback strategies</li>
 *   <li>Comprehensive error handling and validation</li>
 *   <li>Integration with existing C4 tooling and visualization platforms</li>
 * </ul>
 * 
 * <p>The generator has evolved from Avatar-specific tooling to a general-purpose
 * architectural documentation system suitable for diverse Java projects including
 * Spring applications, OSGi systems, microservices, and traditional enterprise applications.</p>
 * 
 * <p><strong>Configuration-driven approach:</strong> All discovery logic is externalized
 * to JSON configuration files, making the system adaptable to different project
 * structures and architectural patterns without requiring code changes.</p>
 * 
 * @see StrategyConfiguration for discovery strategy configuration
 * @see ConfigurableComponentScanner for component discovery execution
 * @see C4ModelConfig for overall model configuration structure
 * @see JsonSchemaValidator for configuration validation
 * 
 * @author C4 Model Generator Team
 * @version 2.0
 * @since 2025-06-15
 */
public class C4ModelGenerator {
    /**
     * Main entry point for the configurable C4 model generation system.
     * 
     * <p>This method orchestrates the complete architectural documentation generation
     * process, combining static configuration with dynamic code analysis to produce
     * comprehensive C4 models. The process is designed to be robust, providing
     * detailed logging and error handling throughout execution.</p>
     * 
     * <p><strong>Execution workflow:</strong></p>
     * <ol>
     *   <li><strong>Configuration loading</strong> - Loads and validates C4 model configuration from JSON</li>
     *   <li><strong>Schema validation</strong> - Ensures configuration integrity using JSON schema</li>
     *   <li><strong>Workspace creation</strong> - Establishes Structurizr workspace with metadata</li>
     *   <li><strong>Model construction</strong> - Creates users, systems, and containers from configuration</li>
     *   <li><strong>Relationship establishment</strong> - Builds connections between model elements</li>
     *   <li><strong>Strategy-based discovery</strong> - Applies configurable component discovery strategies</li>
     *   <li><strong>Component enrichment</strong> - Enhances discovered components with additional metadata</li>
     *   <li><strong>View generation</strong> - Creates C4 views for different architectural levels</li>
     *   <li><strong>Output generation</strong> - Exports the complete model in Structurizr JSON format</li>
     * </ol>
     * 
     * <p><strong>Key innovations:</strong></p>
     * <ul>
     *   <li>Automated container key mapping with multiple fallback strategies</li>
     *   <li>Project-agnostic configuration system</li>
     *   <li>Comprehensive error handling and recovery</li>
     *   <li>Detailed execution logging for debugging and monitoring</li>
     * </ul>
     * 
     * <p>The system supports any Java project structure and can be easily configured
     * for different architectural patterns including Spring applications, OSGi systems,
     * microservices, and traditional enterprise applications.</p>
     * 
     * @param args Command line arguments (currently unused - all configuration is file-based)
     * @throws Exception If critical errors occur during model generation, configuration loading,
     *                  or file I/O operations. Non-critical errors are logged but don't halt execution.
     * 
     * @see ConfigurableComponentScanner#scanContainer for component discovery process
     */
    public static void main(String[] args) throws Exception {
        System.out.println("=== Starting Avatar C4 Model Generation ===");
        
        // Load complete C4 model configuration from JSON
        File c4ConfigJson = new File("src/main/java/org/example/json/c4ModelConfig.json");
        System.out.println("Loading C4 configuration from: " + c4ConfigJson.getAbsolutePath());
        
        // Load with schema validation enabled
        C4ModelConfig c4Config = C4ModelConfig.loadFromFile(c4ConfigJson, true);
        System.out.println("✓ C4 configuration loaded and validated successfully");
        
        // Create workspace with configured metadata
        WorkspaceDetail workspaceConfig = c4Config.getWorkspace();
        Workspace workspace = new Workspace(workspaceConfig.getName(), workspaceConfig.getDescription());
        Model model = workspace.getModel();
        ViewSet views = workspace.getViews();
        
        System.out.println("\n=== WORKSPACE CREATED ===");
        System.out.println("Name: " + workspaceConfig.getName());
        System.out.println("Description: " + workspaceConfig.getDescription());


        // Create persons from configuration
        System.out.println("\n=== CREATING PERSONS ===");
        Map<String, Person> persons = new HashMap<>();
        for (PersonDetail personConfig : c4Config.getPersons()) {
            Person person = model.addPerson(personConfig.getName(), personConfig.getDescription());
            persons.put(personConfig.getName(), person);
            System.out.println("✓ Person created: " + personConfig.getName() + " - " + personConfig.getDescription());
        }
        System.out.println("Total persons created: " + persons.size());

        // Create software systems from configuration
        System.out.println("\n=== CREATING SOFTWARE SYSTEMS ===");
        Map<String, SoftwareSystem> softwareSystems = new HashMap<>();
        for (SoftwareSystemDetail systemConfig : c4Config.getSoftwareSystems()) {
            SoftwareSystem system = model.addSoftwareSystem(systemConfig.getName(), systemConfig.getDescription());
            softwareSystems.put(systemConfig.getName(), system);
            System.out.println("✓ Software System created: " + systemConfig.getName() + " - " + systemConfig.getDescription());
        }
        System.out.println("Total software systems created: " + softwareSystems.size());

        // Create containers from configuration
        System.out.println("\n=== CREATING CONTAINERS ===");
        Map<String, Container> containers = new HashMap<>();
        for (ContainerConfigDetail containerConfig : c4Config.getContainers()) {
            String systemName = containerConfig.getSoftwareSystemName();
            SoftwareSystem parentSystem = softwareSystems.get(systemName);

            if (parentSystem == null) {
                // fallback to the first system (or handle as you prefer)
                parentSystem = softwareSystems.values().iterator().next();
                System.out.println("⚠ Warning: Software system '" + systemName + "' not found. Using default: "
                        + parentSystem.getName());
            }

            Container container = parentSystem.addContainer(
                    containerConfig.getName(),
                    containerConfig.getDescription(),
                    containerConfig.getTechnology()
            );
            containers.put(containerConfig.getName(), container);

            System.out.println("✓ Container created: " + containerConfig.getName() +
                    " - " + containerConfig.getDescription() +
                    " (Technology: " + containerConfig.getTechnology() + ")" +
                    " [Parent System: " + parentSystem.getName() + "]");
        }

        System.out.println("Total containers created: " + containers.size());

        // Establish person relationships
        establishPersonRelationships(c4Config, persons, softwareSystems, containers);

        // Establish additional person-container relationships from configuration
        System.out.println("\n=== ESTABLISHING ADDITIONAL PERSON-CONTAINER RELATIONSHIPS ===");
        establishPersonContainerRelationships(c4Config, persons, containers);

        // Establish software system relationships
        establishSoftwareSystemRelationships(c4Config, softwareSystems);

        // Establish container relationships
        establishContainerRelationships(c4Config, containers);


        // Load component mapper configuration for enrichment (automated from config)
        System.out.println("\n=== LOADING COMPONENT CONFIGURATIONS ===");
        Map<String, ContainerDetail> allContainers = c4Config.getContainerMap();
        System.out.println("✓ Component mapper configurations loaded: " + allContainers.size() + " containers");
        
        // Dynamically extract component maps for all containers in config
        Map<String, Map<String, ComponentDetail>> containerComponentMaps = new HashMap<>();
        
        for (Map.Entry<String, ContainerDetail> entry : allContainers.entrySet()) {
            String containerKey = entry.getKey();
            ContainerDetail containerDetail = entry.getValue();
            
            if (containerDetail != null) {
                Map<String, ComponentDetail> componentMap = containerDetail.getComponentMap();
                containerComponentMaps.put(containerKey, componentMap);
                System.out.println("✓ " + containerKey + " component config loaded: " + 
                                 (componentMap != null ? componentMap.size() : 0) + " components");
            }
        }

        // Load strategy configuration
        System.out.println("\n=== LOADING STRATEGY CONFIGURATION ===");
        File strategyConfigJson = new File("src/main/java/org/example/json/strategyConfig.json");
        StrategyConfiguration strategyConfig = StrategyConfiguration.loadFromFile(strategyConfigJson);
        System.out.println("✓ Strategy configuration loaded from: " + strategyConfigJson.getAbsolutePath());
        
        // Create configurable component scanner
        ConfigurableComponentScanner scanner = new ConfigurableComponentScanner(strategyConfig);
        System.out.println("✓ Component scanner initialized");

        // Get containers dynamically from configuration for component scanning
        System.out.println("\n=== PREPARING CONTAINERS FOR COMPONENT SCANNING ===");
        Map<String, Container> containersForScanning = new HashMap<>();
        
        // Map container names from config to actual containers
        for (ContainerConfigDetail containerConfig : c4Config.getContainers()) {
            Container container = containers.get(containerConfig.getName());
            if (container != null) {
                containersForScanning.put(containerConfig.getName(), container);
                System.out.println("- " + containerConfig.getName() + ": ✓");
            } else {
                System.out.println("- " + containerConfig.getName() + ": ✗ (not found)");
            }
        }
        

        // Scan all containers automatically using configured strategies
        System.out.println("\n=== SCANNING CONTAINERS FOR COMPONENTS ===");
        for (Map.Entry<String, Container> entry : containersForScanning.entrySet()) {
            String containerName = entry.getKey();
            Container container = entry.getValue();
            
            // Find corresponding container key in the component configuration
            String containerKey = findContainerKeyInConfig(containerName, allContainers);
            Map<String, ComponentDetail> componentMap = containerComponentMaps.get(containerKey);
            
            System.out.println("Scanning container: " + container.getName() + 
                             " (config key: " + containerKey + ")");
            
            scanner.scanContainer(container, containerKey, componentMap);
            System.out.println("✓ Completed scanning: " + container.getName() + 
                             " (" + container.getComponents().size() + " components)");
        }

        // Create views
        System.out.println("\n=== CREATING VIEWS ===");
        
        // Create system context views for each software system
        int systemContextViewCount = 0;
        for (Map.Entry<String, SoftwareSystem> entry : softwareSystems.entrySet()) {
            String systemName = entry.getKey();
            SoftwareSystem system = entry.getValue();
            if(system.getContainers().size() == 0) {
                System.out.println("⚠ Skipping system context view for " + systemName +
                                 " (no containers found)");
                continue;
            }
            
            String viewKey = generateSystemContextViewKey(systemName);
            String viewTitle = systemName + " - System Context";
            
            SystemContextView systemContextView = views.createSystemContextView(system, viewKey, viewTitle);
            systemContextView.addAllElements();
            System.out.println("✓ System context view created for: " + system.getName());
            systemContextViewCount++;
        }
        System.out.println("Total system context views created: " + systemContextViewCount);
        
        // Create container views for each software system
        int containerViewCount = 0;
        for (Map.Entry<String, SoftwareSystem> entry : softwareSystems.entrySet()) {
            String systemName = entry.getKey();
            SoftwareSystem system = entry.getValue();

            if(system.getContainers().size() == 0) {
                System.out.println("⚠ Skipping Container context view for " + systemName +
                        " (no containers found)");
                continue;
            }
            
            String viewKey = generateContainerViewKey(systemName);
            String viewTitle = systemName + " - Containers";

            ContainerView containerView = views.createContainerView(system, viewKey, viewTitle);
            containerView.addAllContainers();


            System.out.println("✓ Container view created for: " + system.getName() + 
                             " (" + system.getContainers().size() + " containers)");
            containerViewCount++;
        }
        System.out.println("Total container views created: " + containerViewCount);

        // Create component views automatically for all scanned containers
        int componentViewCount = 0;
        for (Map.Entry<String, Container> entry : containersForScanning.entrySet()) {
            String containerName = entry.getKey();
            Container container = entry.getValue();

            if (container.getComponents().size() > 0) {
                String viewKey = generateContainerViewKey(containerName);
                String viewTitle = containerName + " Components";

                ComponentView componentView = views.createComponentView(container, viewKey, viewTitle);
                componentView.addAllComponents();

                System.out.println("✓ Component view created for: " + container.getName() +
                                 " (" + container.getComponents().size() + " components)");
                componentViewCount++;
            } else {
                System.out.println("⚠ Skipping component view for " + container.getName() +
                                 " (no components found)");
            }
        }
        System.out.println("Total component views created: " + componentViewCount);
        System.out.println("Total component views created: " + componentViewCount);

        // Add styles
        System.out.println("\n=== APPLYING STYLES ===");
        Styles styles = views.getConfiguration().getStyles();
        styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);
        styles.addElementStyle(Tags.CONTAINER).background("#438dd5").color("#ffffff");

        styles.addElementStyle("Info").background("#85bb65").color("#ffffff");
        styles.addElementStyle("Request").background("#7560ba").color("#ffffff");
        styles.addElementStyle("Response").background("#e6bd56").color("#000000");
        styles.addElementStyle("Result").background("#b86950").color("#ffffff");
        styles.addElementStyle("Endpoint").background("#60aa9f").color("#ffffff");
        styles.addElementStyle("API").background("#4b79cc").color("#ffffff");
        styles.addElementStyle("Implementation").background("#f5da55").color("#000000");
        styles.addElementStyle("Infrastructure").background("#b86950").color("#ffffff");
        System.out.println("✓ Element styles applied for visualization");

        // Export to JSON
        System.out.println("\n=== EXPORTING MODEL ===");
        try (Writer writer = new FileWriter("avatar-c4-model.json")) {
            new JsonWriter(true).write(workspace, writer);
            System.out.println("✓ Avatar C4 model exported to avatar-c4-model.json");
        }
        
        // NEW: Component change detection and serialization
        System.out.println("\n=== COMPONENT CHANGE DETECTION ===");
        try {
            // Validate containers have components
            if (ComponentChangeDetector.validateContainersHaveComponents(containers)) {
                // Perform change detection
                ComponentSerializationService.ComponentComparisonResult changeResult = 
                    ComponentChangeDetector.detectChanges(containers);
                
                // Generate change report
                String changeReport = ComponentChangeDetector.generateChangeReport(changeResult);
                System.out.println(changeReport);
                
                // Set exit code based on changes (useful for CI/CD)
                if (changeResult.hasChanges) {
                    System.out.println("\n🚀 Changes detected - CI/CD pipeline should regenerate documentation");
                    // You can use System.exit(1) here if you want the CI to detect changes via exit code
                } else {
                    System.out.println("\n✅ No changes detected - documentation is up to date");
                }
            } else {
                System.out.println("⚠ Skipping change detection due to validation issues");
            }
        } catch (Exception e) {
            System.out.println("⚠ Component change detection failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Summary statistics
        System.out.println("\n=== GENERATION SUMMARY ===");
        System.out.println("Workspace: " + workspaceConfig.getName());
        System.out.println("Persons: " + persons.size());
        System.out.println("Software Systems: " + softwareSystems.size());
        System.out.println("Containers: " + containers.size());
        
        int totalComponents = 0;
        for (Container container : containers.values()) {
            totalComponents += container.getComponents().size();
        }
        System.out.println("Total Components: " + totalComponents);
        System.out.println("System Context Views: " + systemContextViewCount);
        System.out.println("Container Views: " + containerViewCount);
        System.out.println("Component Views: " + componentViewCount);
        System.out.println("=== Avatar C4 Model Generation Completed Successfully ===");
    }


    /**
     * Establishes additional person-container relationships that may not be directly configured.
     * 
     * This method handles special cases where persons need to interact with specific containers
     * based on business logic or common patterns in the system architecture.
     * 
     * @param c4Config The complete C4 model configuration
     * @param persons Map of person names to Person objects
     * @param containers Map of container names to Container objects
     */
    private static void establishPersonContainerRelationships(
            C4ModelConfig c4Config, 
            Map<String, Person> persons, 
            Map<String, Container> containers) {
        
        int additionalRelationCount = 0;
        
        // Example: Client User sends requests to Connector Implementations
        Person clientUser = persons.get("Client User");
        Container connectorImplementations = containers.get("Connector Implementations");
        
        if (clientUser != null && connectorImplementations != null) {
            clientUser.uses(connectorImplementations, "Sends requests to");
            System.out.println("✓ Additional Person-Container Relationship: " + clientUser.getName() + 
                             " -> " + connectorImplementations.getName() + " (Sends requests to)");
            additionalRelationCount++;
        } else {
            if (clientUser == null) {
                System.out.println("⚠ Warning: Client User not found for additional relationship");
            }
            if (connectorImplementations == null) {
                System.out.println("⚠ Warning: Connector Implementations container not found for additional relationship");
            }
        }
        
        System.out.println("Total additional person-container relationships established: " + additionalRelationCount);
    }

    /**
     * Assigns component metadata and relationships from JSON configuration to discovered components.
     *
     * This method iterates through all components in the container and matches them with
     * their corresponding configuration entries in the component map. When a match is found
     * (based on component name substring matching), it applies the metadata (technology,
     * tags, description) and establishes relationships to other components.
     *
     * The relationship establishment includes validation to ensure target components exist
     * before creating the relationship links.
     *
     * @param container The container whose components need to be enriched
     * @param componentMap Map of component identifiers to their detailed configurations
     */
    public static void assignRealtionFromJson(Container container, Map<String, ComponentDetail> componentMap) {
        if (componentMap == null || componentMap.isEmpty()) {
            return;
        }

        for (Component component : container.getComponents()) {
            for (Map.Entry<String, ComponentDetail> entry : componentMap.entrySet()) {
                String keySubstring = entry.getKey().toLowerCase().trim();
                ComponentDetail detail = entry.getValue();

                String componentName = component.getName().toLowerCase().trim();

                if (componentName.equals(keySubstring)) {
                    // Apply metadata
                    if (detail.getTechnology() != null) {
                        component.setTechnology(detail.getTechnology());
                    }
                    if (detail.getTags() != null) {
                        component.addTags(detail.getTags());
                    }
                    if (detail.getDescription() != null) {
                        component.setDescription(detail.getDescription());
                    }

                    // Apply relationships
                    List<Relations> relations = detail.getRelations();
                    if (relations != null && !relations.isEmpty()) {

                        for (Relations relation : relations) {
                            System.out.println("Processing relation: " + relation.getType() + " to " + relation.getTarget());
                            Component targetComponent = container.getComponentWithName(relation.getTarget());
                            if (targetComponent != null) {
                                component.uses(targetComponent, relation.getType());
                                System.out.println("Added relation from " + component.getName() + " to " + targetComponent.getName() + " of type " + relation.getType());
                            } else {
                                System.out.println("Target component " + relation.getTarget() + " not found for relation in " + component.getName());
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * Finds the corresponding container key in the component configuration for a given container name.
     * This method uses automated matching strategies to work with any project configuration.
     * 
     * @param containerName The display name of the container
     * @param allContainers Map of all container configurations
     * @return The configuration key for the container, or null if not found
     */
    private static String findContainerKeyInConfig(String containerName, Map<String, ContainerDetail> allContainers) {
        if (containerName == null || allContainers == null || allContainers.isEmpty()) {
            return null;
        }
        
        String normalizedContainerName = containerName.toLowerCase().trim();
        
        // Strategy 1: Exact match (case-insensitive)
        for (String key : allContainers.keySet()) {
            if (key.toLowerCase().trim().equals(normalizedContainerName)) {
                System.out.println("✓ Found exact match: " + containerName + " -> " + key);
                return key;
            }
        }
        
        // Strategy 2: Exact match without spaces and special characters
        String cleanContainerName = normalizedContainerName.replaceAll("[\\s\\-_]", "");
        for (String key : allContainers.keySet()) {
            String cleanKey = key.toLowerCase().replaceAll("[\\s\\-_]", "");
            if (cleanKey.equals(cleanContainerName)) {
                System.out.println("✓ Found normalized match: " + containerName + " -> " + key);
                return key;
            }
        }
        
        // Strategy 3: Key contains container name (without spaces)
        for (String key : allContainers.keySet()) {
            String cleanKey = key.toLowerCase().replaceAll("[\\s\\-_]", "");
            if (cleanKey.contains(cleanContainerName)) {
                System.out.println("✓ Found key containing container name: " + containerName + " -> " + key);
                return key;
            }
        }
        
        // Strategy 4: Container name contains key (without spaces)
        for (String key : allContainers.keySet()) {
            String cleanKey = key.toLowerCase().replaceAll("[\\s\\-_]", "");
            if (cleanContainerName.contains(cleanKey)) {
                System.out.println("✓ Found container name containing key: " + containerName + " -> " + key);
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
                    if (containerWord.equals(keyWord) && containerWord.length() > 2) // Avoid matching very short words
                        System.out.println("✓ Found word match: " + containerName + " -> " + key + " (matched word: " + containerWord + ")");
                    return key;
                }
            }
        }
        
        // Strategy 6: Fuzzy matching - check if key is a substring of container name or vice versa
        for (String key : allContainers.keySet()) {
            String cleanKey = key.toLowerCase().replaceAll("[\\s\\-_]", "");
            if (cleanKey.length() > 3 && (cleanContainerName.contains(cleanKey) || cleanKey.contains(cleanContainerName))) {
                System.out.println("✓ Found fuzzy match: " + containerName + " -> " + key);
                return key;
            }
        }
        
        System.out.println("⚠ Warning: No configuration key found for container: " + containerName);
        System.out.println("Available keys: " + allContainers.keySet());
        return null;
    }
    
    /**
     * Generates a unique view key from a system name for system context views.
     * 
     * @param systemName The name of the software system
     * @return A sanitized view key suitable for Structurizr
     */
    private static String generateSystemContextViewKey(String systemName) {
        return systemName.toLowerCase()
                .replace(" ", "-")
                .replace("_", "-")
                .replaceAll("[^a-z0-9-]", "")
                + "-context";
    }
    
    /**
     * Generates a unique view key from a system name for container views.
     * 
     * @param systemName The name of the software system
     * @return A sanitized view key suitable for Structurizr
     */
    private static String generateContainerViewKey(String systemName) {
        return systemName.toLowerCase()
                .replace(" ", "-")
                .replace("_", "-")
                .replaceAll("[^a-z0-9-]", "")
                + "-containers";
    }

    /**
     * Establishes person relationships from configuration.
     * 
     * This method creates relationships between persons and their target systems or containers
     * as defined in the configuration file.
     * 
     * @param c4Config The complete C4 model configuration
     * @param persons Map of person names to Person objects
     * @param softwareSystems Map of software system names to SoftwareSystem objects
     * @param containers Map of container names to Container objects
     */
    private static void establishPersonRelationships(
            C4ModelConfig c4Config,
            Map<String, Person> persons,
            Map<String, SoftwareSystem> softwareSystems,
            Map<String, Container> containers) {
        
        System.out.println("\n=== ESTABLISHING PERSON RELATIONSHIPS ===");
        int personRelationCount = 0;
        
        for (PersonDetail personConfig : c4Config.getPersons()) {
            Person person = persons.get(personConfig.getName());
            if (personConfig.getRelations() != null) {
                for (Relations relation : personConfig.getRelations()) {
                    SoftwareSystem targetSystem = softwareSystems.get(relation.getTarget());
                    Container targetContainer = containers.get(relation.getTarget());
                    
                    if (targetSystem != null) {
                        person.uses(targetSystem, relation.getType());
                        System.out.println("✓ Person-System Relationship: " + person.getName() + 
                                         " -> " + targetSystem.getName() + " (" + relation.getType() + ")");
                        personRelationCount++;
                    } else if (targetContainer != null) {
                        person.uses(targetContainer, relation.getType());
                        System.out.println("✓ Person-Container Relationship: " + person.getName() + 
                                         " -> " + targetContainer.getName() + " (" + relation.getType() + ")");
                        personRelationCount++;
                    } else {
                        System.out.println("⚠ Warning: Target not found for person relation: " + 
                                         person.getName() + " -> " + relation.getTarget());
                    }
                }
            }
        }
        System.out.println("Total person relationships established: " + personRelationCount);
    }

    /**
     * Establishes software system relationships from configuration.
     * 
     * This method creates relationships between software systems as defined in the configuration file.
     * 
     * @param c4Config The complete C4 model configuration
     * @param softwareSystems Map of software system names to SoftwareSystem objects
     */
    private static void establishSoftwareSystemRelationships(
            C4ModelConfig c4Config,
            Map<String, SoftwareSystem> softwareSystems) {
        
        System.out.println("\n=== ESTABLISHING SOFTWARE SYSTEM RELATIONSHIPS ===");
        int systemRelationCount = 0;
        
        for (SoftwareSystemDetail systemConfig : c4Config.getSoftwareSystems()) {
            SoftwareSystem system = softwareSystems.get(systemConfig.getName());
            if (systemConfig.getRelations() != null) {
                for (Relations relation : systemConfig.getRelations()) {
                    SoftwareSystem targetSystem = softwareSystems.get(relation.getTarget());
                    if (targetSystem != null) {
                        system.uses(targetSystem, relation.getType());
                        System.out.println("✓ System-System Relationship: " + system.getName() + 
                                         " -> " + targetSystem.getName() + " (" + relation.getType() + ")");
                        systemRelationCount++;
                    } else {
                        System.out.println("⚠ Warning: Target system not found for relation: " + 
                                         system.getName() + " -> " + relation.getTarget());
                    }
                }
            }
        }
        System.out.println("Total software system relationships established: " + systemRelationCount);
    }

    /**
     * Establishes container relationships from configuration.
     * 
     * This method creates relationships between containers as defined in the configuration file.
     * 
     * @param c4Config The complete C4 model configuration
     * @param containers Map of container names to Container objects
     */
    private static void establishContainerRelationships(
            C4ModelConfig c4Config,
            Map<String, Container> containers) {
        
        System.out.println("\n=== ESTABLISHING CONTAINER RELATIONSHIPS ===");
        int containerRelationCount = 0;
        
        for (ContainerConfigDetail containerConfig : c4Config.getContainers()) {
            Container container = containers.get(containerConfig.getName());
            if (containerConfig.getRelations() != null) {
                for (Relations relation : containerConfig.getRelations()) {
                    Container targetContainer = containers.get(relation.getTarget());
                    if (targetContainer != null) {
                        container.uses(targetContainer, relation.getType());
                        System.out.println("✓ Container-Container Relationship: " + container.getName() + 
                                         " -> " + targetContainer.getName() + " (" + relation.getType() + ")");
                        containerRelationCount++;
                    } else {
                        System.out.println("⚠ Warning: Target container not found for relation: " + 
                                         container.getName() + " -> " + relation.getTarget());
                    }
                }
            }
        }
        System.out.println("Total container relationships established: " + containerRelationCount);
    }


}