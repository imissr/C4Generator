package org.example.c4;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.structurizr.Workspace;
import com.structurizr.component.ComponentFinder;
import com.structurizr.component.ComponentFinderBuilder;
import com.structurizr.component.ComponentFinderStrategyBuilder;
import com.structurizr.component.matcher.AnnotationTypeMatcher;
import com.structurizr.component.matcher.NameSuffixTypeMatcher;
import com.structurizr.component.matcher.RegexTypeMatcher;
import com.structurizr.io.json.JsonWriter;
import com.structurizr.model.*;
import com.structurizr.model.Component;
import com.structurizr.view.*;
import org.example.componentDetail.*;

/**
 * Avatar C4 Model Generator - Main application for generating C4 architecture models.
 * 
 * This class generates a comprehensive C4 model for the Avatar Connector System,
 * which is a connector-based system for healthcare data exchange. The model includes:
 * - System context views showing users and external systems
 * - Container views showing the main architectural containers
 * - Component views showing internal components within each container
 * - Automated component discovery using OSGi annotations
 * - Component relationships and dependencies
 * 
 * The generated model is exported as a JSON file compatible with Structurizr for
 * visualization and documentation purposes.
 * 
 * @author Generated Documentation
 * @version 1.0
 * @since 2025-06-15
 */
public class AvatarC4ModelGenerator {
    /**
     * Main entry point for the Avatar C4 Model Generator application.
     * 
     * This method orchestrates the entire C4 model generation process:
     * 1. Loads complete C4 model configuration from JSON file
     * 2. Creates a Structurizr workspace with configured metadata
     * 3. Automatically creates users, systems, and containers from configuration
     * 4. Establishes relationships as defined in configuration
     * 5. Performs automated component discovery
     * 6. Creates container and component views
     * 7. Applies styling and exports the model to JSON
     * 
     * @param args Command line arguments (currently unused)
     * @throws Exception If model generation fails due to file I/O or component scanning errors
     */
    public static void main(String[] args) throws Exception {
        System.out.println("=== Starting Avatar C4 Model Generation ===");
        
        // Load complete C4 model configuration from JSON
        File c4ConfigJson = new File("src/main/java/org/example/json/c4ModelConfig.json");
        System.out.println("Loading C4 configuration from: " + c4ConfigJson.getAbsolutePath());
        C4ModelConfig c4Config = C4ModelConfig.loadFromFile(c4ConfigJson);
        System.out.println("✓ C4 configuration loaded successfully");
        
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
            // Find the parent software system (assuming first software system for now)
            SoftwareSystem parentSystem = softwareSystems.values().iterator().next();
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

        // Establish additional person-container relationships from configuration
        System.out.println("\n=== ESTABLISHING ADDITIONAL PERSON-CONTAINER RELATIONSHIPS ===");
        establishPersonContainerRelationships(c4Config, persons, containers);

        // Establish software system relationships
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

        // Establish container relationships
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
        
        System.out.println("Total containers available for scanning: " + containersForScanning.size());

        // Create API components automatically for containers marked as API
        System.out.println("\n=== CREATING API COMPONENTS ===");
        for (Map.Entry<String, Container> entry : containersForScanning.entrySet()) {
            String containerName = entry.getKey();
            Container container = entry.getValue();
            
            // Check if this container should have manual API components created
            // This could be made configurable in the future
            if (containerName.toLowerCase().contains("api")) {
                createApiComponents(container);
                System.out.println("✓ API components created for: " + container.getName());
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

        // Create container view
        System.out.println("\n=== CREATING VIEWS ===");
        SoftwareSystem avatarSystem = softwareSystems.values().iterator().next(); // Get the first system
        ContainerView containerView = views.createContainerView(avatarSystem, "containers", "Container View");
        containerView.addAllElements();
        System.out.println("✓ Container view created for: " + avatarSystem.getName());
        
        // Add all persons to container view
        for (Person person : persons.values()) {
            containerView.add(person);
        }
        System.out.println("✓ Added " + persons.size() + " persons to container view");

        // Create component views automatically for all scanned containers
        int componentViewCount = 0;
        for (Map.Entry<String, Container> entry : containersForScanning.entrySet()) {
            String containerName = entry.getKey();
            Container container = entry.getValue();
            
            if (container.getComponents().size() > 0) {
                String viewKey = generateViewKey(containerName);
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
        System.out.println("Component Views: " + componentViewCount);
        System.out.println("=== Avatar C4 Model Generation Completed Successfully ===");
    }
    /**
     * Creates manual API components for the connector API container.
     * 
     * This method manually defines the core API interfaces that form the
     * contract for all connector implementations in the Avatar system.
     * These components represent the fundamental interfaces that all
     * connector implementations must adhere to.
     * 
     * @param container The connector API container to add components to
     */
    // Method to manually create API components based on documentation
    private static void createApiComponents(Container container) {
        System.out.println("Creating API components for container: " + container.getName());
        
        Component avatarConnectorInfo = container.addComponent("AvatarConnectorInfo",
                "Base interface providing metadata about connectors", "Java Interface");
        avatarConnectorInfo.addTags("API");
        System.out.println("✓ API Component created: " + avatarConnectorInfo.getName() + " - " + avatarConnectorInfo.getDescription());

        Component avatarConnector = container.addComponent("AvatarConnector",
                "Main service interface for connector implementations", "Java Interface");
        avatarConnector.addTags("API");
        System.out.println("✓ API Component created: " + avatarConnector.getName() + " - " + avatarConnector.getDescription());

        avatarConnector.uses(avatarConnectorInfo, "extends");
        System.out.println("✓ API Component Relationship: " + avatarConnector.getName() + 
                         " -> " + avatarConnectorInfo.getName() + " (extends)");
        
        System.out.println("Total API components created: " + container.getComponents().size());
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
     * This method handles the mapping between container display names and their configuration keys.
     * 
     * @param containerName The display name of the container
     * @param allContainers Map of all container configurations
     * @return The configuration key for the container, or null if not found
     */
    private static String findContainerKeyInConfig(String containerName, Map<String, ContainerDetail> allContainers) {
        // Direct mapping for known containers
        Map<String, String> containerNameToKeyMapping = new HashMap<>();
        containerNameToKeyMapping.put("Connector Model", "connectorModel");
        containerNameToKeyMapping.put("Connector API", "connectorApi");
        containerNameToKeyMapping.put("Connector Implementations", "connectorImplementations");
        containerNameToKeyMapping.put("Infrastructure", "connectorInfrastructure");
        
        String directKey = containerNameToKeyMapping.get(containerName);
        if (directKey != null && allContainers.containsKey(directKey)) {
            return directKey;
        }
        
        // Fallback: try to find by partial name matching
        String lowerContainerName = containerName.toLowerCase();
        for (String key : allContainers.keySet()) {
            if (key.toLowerCase().contains(lowerContainerName.replace(" ", "").toLowerCase()) ||
                lowerContainerName.replace(" ", "").toLowerCase().contains(key.toLowerCase())) {
                return key;
            }
        }
        
        System.out.println("⚠ Warning: No configuration key found for container: " + containerName);
        return null;
    }
    
    /**
     * Generates a unique view key from a container name for component views.
     * 
     * @param containerName The name of the container
     * @return A sanitized view key suitable for Structurizr
     */
    private static String generateViewKey(String containerName) {
        return containerName.toLowerCase()
                .replace(" ", "-")
                .replace("_", "-")
                .replaceAll("[^a-z0-9-]", "")
                + "-components";
    }

}