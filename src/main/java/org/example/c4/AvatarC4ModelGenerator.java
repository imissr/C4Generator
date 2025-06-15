package org.example.c4;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

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


        String basePathModel = "C:\\Users\\khale\\IdeaProjects\\avatar-dataspaces-demo\\de.avatar.connector.model";
        String basePathPorject = "C:\\Users\\khale\\IdeaProjects\\avatar-dataspaces-demo\\";


        File json = new File("src/main/java/org/example/json/componentMapper.json");

        ContainerConfig config = ContainerConfig.loadFromFile(json);
        Map<String, ContainerDetail> allContainers = config.getContainerMap();
        Map<String, ComponentDetail> componentMap = null;
        Map<String, ComponentDetail> componentConnectorMap = null;
        Map<String, ComponentDetail> componentInfrastructureMap = null;
        ContainerDetail connectorModelDetail = allContainers.get("connectorModel");
        ContainerDetail connectorImplementationDetail = allContainers.get("connectorImplementations");
        ContainerDetail connectorInfrastructureDetail = allContainers.get("connectorInfrastructure");



        if (connectorModelDetail != null) {
            componentMap = connectorModelDetail.getComponentMap();
            componentConnectorMap = connectorImplementationDetail.getComponentMap();
            componentInfrastructureMap = connectorInfrastructureDetail.getComponentMap();
        }



        createApiComponents(connectorApi);

        tryScanningForComponents(connectorImplementations, connectorModel,connectorInfrastructure, basePathModel, basePathPorject, componentMap , componentConnectorMap , componentInfrastructureMap);


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
     * Attempts to scan for components in the specified directories using OSGi annotations.
     * 
     * This method coordinates the component discovery process across multiple containers
     * by scanning the compiled classes in the specified base paths. It uses the component
     * mappings loaded from JSON configuration files to enrich the discovered components
     * with metadata and relationship information.
     * 
     * @param container1 The connector implementations container
     * @param container2 The connector model container  
     * @param container3 The infrastructure container
     * @param basePath Base path for the connector model classes
     * @param basePath2 Base path for the broader project structure
     * @param componentMap Component details for the model container
     * @param componentConnectorMap Component details for the connector implementations
     * @param componentInfrastructureMap Component details for the infrastructure container
     */
    private static void tryScanningForComponents(Container container1, Container container2,Container container3, String basePath, String basePath2, Map<String, ComponentDetail> componentMap
            , Map<String, ComponentDetail> componentConnectorMap , Map<String, ComponentDetail> componentInfrastructureMap) {
        File path = new File(basePath);
        File path2 = new File(basePath2);
        if (!path.exists()) {
            System.out.println("Warning: Path " + basePath + " doesn't exist. Skipping component scanning.");
            return;
        }
        try {
            System.out.println("Attempting to scan for components in: " + basePath);

            //tryScanningConnectorByOsgiComponentAnnotation(container1, path2,componentConnectorMap  );
            //tryScanningByOSGiFindAllModel(container2, path, componentMap);
            //findInfrastructureComponents(container3,path2,componentInfrastructureMap);

            tryScanningByOSGiFindAllModelNew(container1, path2, componentConnectorMap);


        } catch (Exception e) {
            System.out.println("Component scanning failed: " + e.getMessage());
        }
    }



    /*private static void findInfrastructureComponents(Container container, File path) {


        try {
            ComponentFinder finder = new ComponentFinderBuilder()
                    .forContainer(container)
                    .fromClasses(path)
                    // Use only ONE strategy to avoid duplicates
                    .withStrategy(
                            new ComponentFinderStrategyBuilder()
                                    .matchedBy(new RegexTypeMatcher(".*(?:Whiteboard|Serializer|Factory).*"))
                                    .withTechnology("Java/OSGi")
                                    .forEach(component -> {
                                        System.out.println("Found infrastructure component: " + component.getName());
                                        String canonicalName = component.getCanonicalName();

                                        // Only process infrastructure-related packages
                                        if (canonicalName.contains("de.avatar.connector.emf") ||
                                                canonicalName.contains("de.avatar.connector.whiteboard")) {

                                            component.addTags("Infrastructure");

                                            if (canonicalName.contains("Ecore Serializer") || canonicalName.contains("Serializer")) {
                                                component.setDescription("Performs serialization/deserialization of EMF objects");
                                            } else if (canonicalName.contains("Whiteboard")) {
                                                component.setDescription("Implements the OSGi whiteboard pattern for tracking connector services");
                                            } else if (canonicalName.contains("Factory")) {
                                                component.setDescription("Factory component for creating infrastructure services");
                                            }

                                            System.out.println("Found infrastructure component: " + component.getName());
                                        }
                                    })
                                    .build()
                    )
                    .build();

            finder.run();
            System.out.println("Successfully found infrastructure components");

        } catch (Exception e) {
            System.out.println("Infrastructure component scanning failed: " + e.getMessage());
            e.printStackTrace();
        }
    }*/

    private static void findInfrastructureComponents(Container container, File path , Map<String, ComponentDetail> componentMap) {
        try {
            ComponentFinder finder = new ComponentFinderBuilder()
                    .forContainer(container)
                    .fromClasses(path)
                    .withStrategy(
                            new ComponentFinderStrategyBuilder()
                                    // More specific regex that excludes inner/anonymous classes
                                    .matchedBy(new RegexTypeMatcher("^de\\.avatar\\.connector\\.(emf|whiteboard)\\..*(?:Whiteboard|Serializer|Factory)(?!\\$).*"))
                                    .withTechnology("Java/OSGi")
                                    .forEach(component -> {
                                        String canonicalName = component.getCanonicalName();

                                        // Skip inner classes and anonymous classes
                                        if (canonicalName.contains("$")) {
                                            return;
                                        }


                                        System.out.println("Found infrastructure component: " + component.getName());
                                    })
                                    .build()
                    )
                    .build();

            finder.run();

            assignRealtionFromJson(container , componentMap);
            System.out.println("Successfully found infrastructure components");

        } catch (Exception e) {
            System.out.println("Infrastructure component scanning failed: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private static void tryScanningConnectorByOsgiComponentAnnotation(Container container, File path ,  Map<String, ComponentDetail> componentMap) {
        try {
            ComponentFinder finder = new ComponentFinderBuilder()
                    .forContainer(container)
                    .fromClasses(path)
                    .withStrategy(
                            new ComponentFinderStrategyBuilder()
                                    .matchedBy(new NameSuffixTypeMatcher("ConnectorImpl"))
                                    .withTechnology("OSGi Connector")
                                    .forEach(component -> {
                                        System.out.println("Found OSGi component: " + component.getName());
                                    })
                                    .build()
                    )
                    .build();
            finder.run();
            assignRealtionFromJson(container, componentMap);

            System.out.println("Successfully found OSGi components");
        } catch (Exception e) {
            System.out.println("No OSGi components found – " + e.getMessage());
        }

    }


    public static void tryScanningByOSGiFindAllModelNew(
            Container container,
            File path,
            Map<String, ComponentDetail> componentMap
    ) {
        try {
            ComponentFinder finder = new ComponentFinderBuilder()
                    .forContainer(container)
                    .fromClasses(path)
                    .withStrategy(
                            new ComponentFinderStrategyBuilder()
                                    // ← here we use YOUR strategy instead of AnnotationTypeMatcher
                                    .matchedBy(
                                            // look for @Component and require a "connector" property
                                            new NewComponentStratgy(

                                                    "org.osgi.service.component.annotations.Component" ,
                                                    "connector"
                                                    ,"property"
                                            )
                                    )
                                    .withTechnology("OSGi Component")
                                    .forEach(component -> {
                                        System.out.println("Found OSGi component: " + component.getName());
                                    })
                                    .build()
                    )
                    .build();

            finder.run();
            assignRealtionFromJson(container, componentMap);
            System.out.println("Successfully found OSGi components");
        } catch (Exception e) {
            System.out.println("No OSGi components found – " + e.getMessage());
        }
    }
    /**
     * Scans for OSGi components using the ProviderType annotation and applies
     * component details from the provided configuration map.
     * 
     * This method uses the Structurizr ComponentFinder to automatically discover
     * OSGi components marked with the @ProviderType annotation. After discovery,
     * it enriches the found components with detailed metadata (technology, tags,
     * descriptions, and relationships) from the pre-loaded component configuration.
     * 
     * @param container The OSGi container to scan for components
     * @param path Directory or JAR file containing compiled classes to scan
     * @param componentMap Pre-loaded map of component names to their detailed configurations
     */
    public static void tryScanningByOSGiFindAllModel(
            Container container,
            File path,
            Map<String, ComponentDetail> componentMap
    ) {
        try {
            ComponentFinder finder = new ComponentFinderBuilder()
                    .forContainer(container)
                    .fromClasses(path)
                    .withStrategy(
                            new ComponentFinderStrategyBuilder()
                                    .matchedBy(
                                            new AnnotationTypeMatcher(
                                                    "org.osgi.annotation.versioning.ProviderType"
                                            )
                                    )
                                    .withTechnology("OSGi Component")
                                    .forEach(component -> {
                                        System.out.println("Found OSGi component: " + component.getName());
                                    })
                                    .build()
                    )
                    .build();
            finder.run();
            assignRealtionFromJson(container, componentMap);
            System.out.println("Successfully found OSGi components");
        } catch (Exception e) {
            System.out.println("No OSGi components found – " + e.getMessage());
        }
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
        for (Component component : container.getComponents()) {
            for (Map.Entry<String, ComponentDetail> entry : componentMap.entrySet()) {
                String keySubstring = entry.getKey();
                ComponentDetail detail = entry.getValue();

                if (component.getName().contains(keySubstring)) {
                    component.setTechnology(detail.getTechnology());
                    component.addTags(detail.getTags());
                    component.setDescription(detail.getDescription());

                    List<Relations> relations = detail.getRelations();
                    System.out.println(keySubstring + " " + relations);
                    if (relations != null && !relations.isEmpty()) {
                        for (Relations relation : relations) {
                            System.out.println("Relation: " + relation.getType() + " to " + relation.getTarget());
                            Component targetComponent = container.getComponentWithName(relation.getTarget());
                            if (targetComponent != null) {
                                component.uses(targetComponent, relation.getType());
                                System.out.println("Added relation from " + component.getName() + " to " + targetComponent.getName() + " of type " + relation.getType());
                            } else {
                                System.out.println("Target component " + relation.getTarget() + " not found for relation in " + component.getName());
                            }
                        }
                    }
                }
            }
        }
    }

}