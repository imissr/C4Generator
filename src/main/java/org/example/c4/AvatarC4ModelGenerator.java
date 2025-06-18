package org.example.c4;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
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
     * 1. Creates a Structurizr workspace for the Avatar system
     * 2. Defines users, systems, and containers
     * 3. Loads component configurations from JSON files
     * 4. Performs automated component discovery
     * 5. Creates container and component views
     * 6. Applies styling and exports the model to JSON
     * 
     * @param args Command line arguments (currently unused)
     * @throws Exception If model generation fails due to file I/O or component scanning errors
     */
    public static void main(String[] args) throws Exception {
        // Create workspace
        Workspace workspace = new Workspace("Avatar C4 Model", "Component analysis for Avatar Connector System");
        Model model = workspace.getModel();
        ViewSet views = workspace.getViews();

        // Define users and external systems
        Person clientUser = model.addPerson("Client User", "Uses the Avatar system to access healthcare data");

        // Define the main software system
        SoftwareSystem avatarSystem = model.addSoftwareSystem("Avatar System", "Connector-based system for healthcare data exchange");
        clientUser.uses(avatarSystem, "Makes data requests through");

        // Define containers within the system
        Container connectorApi = avatarSystem.addContainer("Connector API",
                "Defines the core contract for connectors", "Java/OSGi");

        Container connectorModel = avatarSystem.addContainer("Connector Model",
                "EMF-based data models", "Java/EMF");

        Container connectorImplementations = avatarSystem.addContainer("Connector Implementations",
                "Implementations of the API for different protocols", "Java/OSGi");

        Container connectorInfrastructure = avatarSystem.addContainer("Infrastructure",
                "Supporting components and services", "Java/OSGi");

        // Define relationships between containers
        connectorImplementations.uses(connectorApi, "Implements");
        connectorImplementations.uses(connectorModel, "Uses");
        connectorInfrastructure.uses(connectorApi, "Supports");
        connectorInfrastructure.uses(connectorModel, "Uses");
        clientUser.uses(connectorImplementations, "Sends requests to");


        // Load component mapper configuration for enrichment
        File componentMapperJson = new File("src/main/java/org/example/json/componentMapper.json");
        ContainerConfig componentConfig = ContainerConfig.loadFromFile(componentMapperJson);
        Map<String, ContainerDetail> allContainers = componentConfig.getContainerMap();
        
        // Extract component maps for enrichment
        Map<String, ComponentDetail> componentMap = null;
        Map<String, ComponentDetail> componentConnectorMap = null;
        Map<String, ComponentDetail> componentInfrastructureMap = null;
        
        ContainerDetail connectorModelDetail = allContainers.get("connectorModel");
        ContainerDetail connectorImplementationDetail = allContainers.get("connectorImplementations");
        ContainerDetail connectorInfrastructureDetail = allContainers.get("connectorInfrastructure");

        if (connectorModelDetail != null) {
            componentMap = connectorModelDetail.getComponentMap();
        }
        if (connectorImplementationDetail != null) {
            componentConnectorMap = connectorImplementationDetail.getComponentMap();
        }
        if (connectorInfrastructureDetail != null) {
            componentInfrastructureMap = connectorInfrastructureDetail.getComponentMap();
        }

        // Load strategy configuration
        File strategyConfigJson = new File("src/main/java/org/example/json/strategyConfig.json");
        StrategyConfiguration strategyConfig = StrategyConfiguration.loadFromFile(strategyConfigJson);
        
        // Create configurable component scanner
        ConfigurableComponentScanner scanner = new ConfigurableComponentScanner(strategyConfig);

        // Create API components manually (could also be configured)
        createApiComponents(connectorApi);

        // Scan containers using configured strategies
        scanner.scanContainer(connectorModel, "connectorModel", componentMap);
        scanner.scanContainer(connectorImplementations, "connectorImplementations", componentConnectorMap);
        scanner.scanContainer(connectorInfrastructure, "connectorInfrastructure", componentInfrastructureMap);
        //scanner.scanContainer(connectorApi, "connectorApi", null);


        // Create container view
        ContainerView containerView = views.createContainerView(avatarSystem, "containers", "Container View");
        containerView.addAllElements();
        containerView.add(clientUser);

        // Create component views for each container
        ComponentView modelComponentView = views.createComponentView(connectorModel,
                "model-components", "Connector Model Components");
        modelComponentView.addAllComponents();

        ComponentView apiComponentView = views.createComponentView(connectorApi,
                "api-components", "Connector API Components");
        apiComponentView.addAllComponents();

        ComponentView implComponentView = views.createComponentView(connectorImplementations,
                "implementation-components", "Connector Implementation Components");
        implComponentView.addAllComponents();

        ComponentView infraComponentView = views.createComponentView(connectorInfrastructure,
                "infrastructure-components", "Infrastructure Components");
        infraComponentView.addAllComponents();

        // Add styles
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

        // Export to JSON
        try (Writer writer = new FileWriter("avatar-c4-model.json")) {
            new JsonWriter(true).write(workspace, writer);
            System.out.println("Avatar C4 model exported to avatar-c4-model.json");
        }
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
        Component avatarConnectorInfo = container.addComponent("AvatarConnectorInfo",
                "Base interface providing metadata about connectors", "Java Interface");
        avatarConnectorInfo.addTags("API");

        Component avatarConnector = container.addComponent("AvatarConnector",
                "Main service interface for connector implementations", "Java Interface");
        avatarConnector.addTags("API");

        avatarConnector.uses(avatarConnectorInfo, "extends");
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
                if(Objects.equals(entry.getKey(), "Ecore Serializer Factory")) {


                }
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
                    break; // Found a match, no need to continue checking other entries
                }
            }
        }
    }

}