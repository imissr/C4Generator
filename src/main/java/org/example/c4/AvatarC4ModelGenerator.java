package org.example.c4;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
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
import org.example.componentDetail.ComponentDetail;
import org.example.componentDetail.ComponentMapperLoader;


public class AvatarC4ModelGenerator {
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

		// Define base path to your Avatar project code
		// Set this to your actual project path or a folder that exists
		String basePathModel = "/home/mohamad-khaled-minawe/Desktop/project/avatar-dataspaces-demo/de.avatar.connector.model";
		String basePathPorject = "/home/mohamad-khaled-minawe/Desktop/project/avatar-dataspaces-demo/";
		String jsonPath = "src/main/java/org/example/json/componentMapper.json";
		Map<String, ComponentDetail> componentMap = ComponentMapperLoader.loadComponentMap(jsonPath);






		//createImplComponents(connectorImplementations, connectorApi, connectorModel);
		createInfraComponents(connectorInfrastructure, connectorModel);
		createApiComponents(connectorApi);

		//scanning for components in the specified base path
		tryScanningForComponents(connectorImplementations , connectorModel , basePathModel , basePathPorject,componentMap);



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

	// Method to manually create implementation components
	/*private static void createImplComponents(Container container, Container api, Container model) {
		Component ismaConnector = container.addComponent("ISMAConnector",
			"Implementation for the ISMA HIMSA protocol", "Java/OSGi");
		ismaConnector.addTags("Implementation");

		Component hl7Connector = container.addComponent("HL7Connector",
			"Implementation for the HL7 healthcare standard", "Java/OSGi");
		hl7Connector.addTags("Implementation");

		//ismaConnector.uses(api.getComponentWithName("AvatarConnector"), "implements");
		//hl7Connector.uses(api.getComponentWithName("AvatarConnector"), "implements");

		ismaConnector.uses(model.getComponentWithName("Endpoint Request"), "processes");
		ismaConnector.uses(model.getComponentWithName("Endpoint Response"), "creates");
		hl7Connector.uses(model.getComponentWithName("Endpoint Request"), "processes");
		hl7Connector.uses(model.getComponentWithName("Endpoint Response"), "creates");
	}*/

	// Method to manually create infrastructure components
	private static void createInfraComponents(Container container, Container model) {
		Component whiteboard = container.addComponent("ConnectorWhiteboard",
			"Implements the OSGi whiteboard pattern for tracking connector services", "Java/OSGi");
		whiteboard.addTags("Infrastructure");

		Component serializer = container.addComponent("EcoreSerializer",
			"Performs serialization/deserialization of EMF objects", "Java/OSGi");
		serializer.addTags("Infrastructure");

		//serializer.uses(model.getComponentWithName("EcoreResult"), "serializes/deserializes");
	}


	// Able to find all components in the specified base path
	private static void tryScanningForComponents(Container container1, Container container2 ,String basePath , String basePath2  , Map<String, ComponentDetail> componentMap) {
		File path = new File(basePath);
		File path2 = new File(basePath2);
		if (!path.exists()) {
			System.out.println("Warning: Path " + basePath + " doesn't exist. Skipping component scanning.");
			return;
		}

		try {
			System.out.println("Attempting to scan for components in: " + basePath);

			tryScanningConnectorByOsgiComponentAnnotation(container1, path2);
			tryScanningByOSGiFindAllModel(container2, path,componentMap);



		} catch (Exception e) {
			System.out.println("Component scanning failed: " + e.getMessage());
		}
	}

	private static void tryScanningBySuffix(Container container, File path, String suffix) {
		try {
			ComponentFinder finder = new ComponentFinderBuilder()
				.forContainer(container)
				.fromClasses(path)
				.withStrategy(
					new ComponentFinderStrategyBuilder()
						.matchedBy(new NameSuffixTypeMatcher(suffix))
						.withTechnology("EMF Model")
						.forEach(component -> {
							component.addTags(suffix);
							System.out.println("Found component by suffix: " + component.getName());
						})
						.build()
				)
				.build();

			finder.run();
			System.out.println("Successfully found components with suffix: " + suffix);
		} catch (Exception e) {
			System.out.println("No components found with suffix: " + suffix + " - " + e.getMessage());
		}
	}



	private static void tryScanningConnectorByOsgiComponentAnnotation(Container container, File path) {
		try {

			ComponentFinder finder = new ComponentFinderBuilder()
					.forContainer(container)
					.fromClasses(path)


					.withStrategy(
							new ComponentFinderStrategyBuilder()
									.matchedBy(new NameSuffixTypeMatcher("ConnectorImpl"))
									.withTechnology("NameSuffix ConnectorImpl")
									.forEach(component -> {
										System.out.println("→ (STRAT 2) Found .*ConnectorImpl.*: " + component.getName());
									})
									.build()
					)

					.build();




			finder.run();
			System.out.println("Successfully found OSGi ConnectorImpl components");
		} catch (Exception e) {
			System.out.println("No matching OSGi components found — " + e.getMessage());
		}
	}





	/**
	 * @param container   the OSGi container
	 * @param path        directory or JAR of compiled classes
	 * @param componentMap   pre‐loaded Map<String, ComponentDetail>
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
										boolean matchedAnything = false;
										for (Map.Entry<String, ComponentDetail> entry : componentMap.entrySet()) {
											String keySubstring = entry.getKey();
											ComponentDetail detail = entry.getValue();

											if (component.getName().contains(keySubstring)) {
												component.setTechnology(detail.getTechnology());
												component.addTags(detail.getTags());
												matchedAnything = true;
												break;
											}
										}

										if (!matchedAnything) {
											System.out.println(
													"Component " + component.getName() + " does not match any known tags"
											);
										}

										System.out.println("Found OSGi component: " + component.getName());
									})
									.build()
					)
					.build();

			finder.run();
			System.out.println("Successfully found OSGi components");
		} catch (Exception e) {
			System.out.println("No OSGi components found – " + e.getMessage());
		}
	}




}
