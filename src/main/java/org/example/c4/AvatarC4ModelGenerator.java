package org.example.c4;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import com.structurizr.Workspace;
import com.structurizr.component.ComponentFinder;
import com.structurizr.component.ComponentFinderBuilder;
import com.structurizr.component.ComponentFinderStrategyBuilder;
import com.structurizr.component.matcher.AnnotationTypeMatcher;
import com.structurizr.component.matcher.NameSuffixTypeMatcher;
import com.structurizr.io.json.JsonWriter;
import com.structurizr.model.*;
import com.structurizr.model.Component;
import com.structurizr.view.*;


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
		String basePath = "C:\\Users\\khale\\IdeaProjects\\avatar-dataspaces-demo\\";
		String basePathOther = "C:\\Users\\khale\\IdeaProjects\\avatar-dataspaces-demo\\de.avatar.connector.isma";
		String basePathIsma = "C:\\Users\\khale\\IdeaProjects\\avatar-dataspaces-demo\\de.avatar.connector.other";

		// Since we may not have the actual source code, let's manually create
		// the components based on the documentation

		createImplComponents(connectorImplementations, connectorApi, connectorModel);
		createInfraComponents(connectorInfrastructure, connectorModel);
		createApiComponents(connectorApi);


		tryScanningForComponents(connectorImplementations ,connectorImplementations , connectorModel,basePath, basePathOther, basePathIsma);



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

	// Method to manually create model components based on documentation


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
	private static void createImplComponents(Container container, Container api, Container model) {
		/*Component ismaConnector = container.addComponent("ISMAConnector",
			"Implementation for the ISMA HIMSA protocol", "Java/OSGi");
		ismaConnector.addTags("Implementation");

		Component hl7Connector = container.addComponent("HL7Connector",
			"Implementation for the HL7 healthcare standard", "Java/OSGi");
		hl7Connector.addTags("Implementation");

		//ismaConnector.uses(api.getComponentWithName("AvatarConnector"), "implements");
		//hl7Connector.uses(api.getComponentWithName("AvatarConnector"), "implements");

	/*	ismaConnector.uses(model.getComponentWithName("Endpoint Request"), "processes");
		ismaConnector.uses(model.getComponentWithName("Endpoint Response"), "creates");
		hl7Connector.uses(model.getComponentWithName("Endpoint Request"), "processes");
		hl7Connector.uses(model.getComponentWithName("Endpoint Response"), "creates");*/
	}

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
	private static void tryScanningForComponents(Container container1, Container container2 , Container container3 ,String basePath ,String basePathOther , String basePathIsma) {
		File path = new File(basePath);
		File pathOther = new File(basePathOther);
		File pathIsma = new File(basePathIsma);
		if (!path.exists()) {
			System.out.println("Warning: Path " + basePath + " doesn't exist. Skipping component scanning.");
			return;
		}

		try {
			System.out.println("Attempting to scan for components in: " + basePath);

			/*Find components by suffix
			List<String> suffixes = List.of("Info", "Request", "Response", "Result", "Endpoint" , "Whiteboard", "Serializer", "Connector", "Implementation", "Infrastructure");
			for (String suffix : suffixes) {
				tryScanningBySuffix(container, path, suffix);
			}*/

			// Find OSGi components by annotation
			tryScanningConnectorByOsgiComponentAnnotation(container1, pathOther);
			tryScanningConnectorByOsgiComponentAnnotation(container2, pathIsma);
			tryScanningByOSGiFindAllModel(container3, path);



		} catch (Exception e) {
			System.out.println("Component scanning failed: " + e.getMessage());
		}
	}
	//possible way is to search after classes suffixes like "Info", "Request", "Response", "Result", "Endpoint" , "Whiteboard", "Serializer", "Connector", "Implementation", "Infrastructure"
	// this way we can find all components that are in the model
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

	//idea: we can scan the bundle to get connectors. one way to make it automatic is to scan for an bundle then
	// find the OSGi components by the @Component annotation with the property "connector" set to true
	// this way we can find all connectors in the bundle -> dont know if this is possible
	// a workaround is just to scan the bundle not the whole src folder
	// and then find the OSGi components by the @Component

	private static void tryScanningConnectorByOsgiComponentAnnotation(Container container, File path) {
		try {
			// Find components with @Component annotation (OSGi DS annotation)
			ComponentFinder finder = new ComponentFinderBuilder()
				.forContainer(container)
				.fromClasses(path)
				.withStrategy(
					new ComponentFinderStrategyBuilder()
						.matchedBy(
							new AnnotationTypeMatcher(
								"org.osgi.service.component.annotations.Component"
							)
						)
						.withTechnology("OSGi Component")
						.forEach(component -> {
							component.addTags("Implementation");
							component.setTechnology("Java/OSGi");
							if(component.getName().contains("ISMA")) {
								component.setDescription("Implementation for the ISMA HIMSA protocol");

							}else if(component.getName().contains("HL7")) {
								component.setDescription("Implementation for the HL7 healthcare standard");

							}
							System.out.println(
								"Found OSGi component: " + component.getName()
							);
						})
						.build()
				)
				.build();

			finder.run();


			System.out.println("Successfully found OSGi components");
		} catch (Exception e) {
			System.out.println("No OSGi components found - " + e.getMessage());
		}
	}




	// Function to find all components in the model using OSGi annotations
	// we can also do so we find only the reuslt by using regex matcher
	// problem is that we also get models that we dont need it like Rsa factory, etc. thats belongs to the emf model
	// good side is that we find every model in that package
	// this stratgegy works because only the model has the @ProviderType annotation -> so based on thiis every new component should have this annoation
	private static void tryScanningByOSGiFindAllModel(Container container, File path) {
		try {
			// Find components with @Component annotation (OSGi DS annotation)
			ComponentFinder finder = new ComponentFinderBuilder()
				.forContainer(container)
				.fromClasses(path)                       // • path → directory/JAR of compiled classes
				.withStrategy(
					new ComponentFinderStrategyBuilder()
						.matchedBy(
							new AnnotationTypeMatcher(
								"org.osgi.annotation.versioning.ProviderType"
							)
						)
						.withTechnology("OSGi Component")
						.forEach(component -> {
							component.setTechnology("EMF Model");
							if(component.getName().contains("ConnectorInfo")) {
								component.addTags("Info");
							} else if(component.getName().contains("EndpointRequest")) {
								component.addTags("Request");

							} else if(component.getName().contains("EndpointResponse")) {
								component.addTags("Response");
							} else if(component.getName().contains("DryRunResult") || component.getName().contains("ErrorResult")) {
								component.addTags("Result");
							} else if(component.getName().contains("ConnectorEndpoint")) {
								component.addTags("Endpoint");
							} else if(component.getName().contains("Package")) {
								component.addTags("Package");
							} else if(component.getName().contains("Serializer")) {
								component.addTags("Infrastructure");
							} else if(component.getName().contains("Connector")) {
								component.addTags("Implementation");
							}else if(component.getName().contains("Parameter")) {
								component.addTags("Parameter");
							} else if(component.getName().contains("Result")) {
								component.addTags("Result");
							} else if(component.getName().contains("Factory")) {
								component.addTags("Factory");
							} else if(component.getName().contains("Metric")) {
								component.addTags("Metric");
							}else if (component.getName().contains("Type")){
								component.addTags("Type");
							}else if (component.getName().contains("Helper")) {
								component.addTags("Helper");
							}
							 else {
								System.out.println(
									"Component " + component.getName() + " does not match any known tags"
								);

							}

							System.out.println(
								"Found OSGi component: " + component.getName()
							);
						})
						.build()
				)
				.build();

			finder.run();


			System.out.println("Successfully found OSGi components");
		} catch (Exception e) {
			System.out.println("No OSGi components found - " + e.getMessage());
		}
	}




}
