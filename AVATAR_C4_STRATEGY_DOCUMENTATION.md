# Avatar C4 Model Generator Strategy Documentation

## Overview

This document provides comprehensive documentation about the component discovery strategies implemented in the Avatar C4 Model Generator project. The system is designed to automatically generate C4 architecture models for the Avatar Connector System, a healthcare data exchange platform built on OSGi principles.

## Development-Focused Documentation Goals

### Making Documentation Easier During Development

The primary aim of this C4 generator is to **make documentation during development easier and more automated**. Traditional approaches to architecture documentation often lag behind actual development, creating a gap between what's implemented and what's documented. Our strategy addresses this by:





### Convention-Based Component Discovery

The system works by identifying and leveraging **existing conventions** in the project:

#### OSGi Convention Example
```java
// Developer adds a new connector implementation
@Component(property = {"connector=new.protocol"})
public class NewProtocolConnectorImpl implements AvatarConnector {
    // Implementation
}
```

**Automatic Discovery Process:**
1. ✅ **Detected**: `NewComponentStrategy` finds the `@Component` annotation with `connector=` property
2. ✅ **Classified**: Automatically assigned to "Connector Implementations" container
3. ✅ **Documented**: Component appears in next C4 model generation
4. ✅ **Styled**: Gets "Implementation" styling and metadata

#### EMF Model Convention Example
```java
// Developer defines a new data model
@ProviderType
public interface NewDataModel extends EObject {
    // Model definition
}
```

**Automatic Discovery Process:**
1. ✅ **Detected**: `AnnotationTypeMatcher` finds `@ProviderType` annotation
2. ✅ **Classified**: Automatically assigned to "Connector Model" container
3. ✅ **Documented**: Appears as EMF model component
4. ✅ **Connected**: JSON configuration can define relationships to other components



#### 3. **LLM-Assisted Discovery** (Future Enhancement)
**Best for**: Projects in mid-development or legacy projects without clear conventions
**Reliability**: Variable (depends on project complexity and LLM training)
**Approach**: Use Large Language Models to analyze project structure and infer relationships

### Future: LLM-Enhanced Component Discovery

We're planning to extend our approach with **LLM-powered analysis** for projects that don't have clear conventions:

#### LLM Discovery Strategy
```python
# Conceptual LLM integration
def llm_discover_components(project_path):
    """
    Use LLM to analyze project structure and identify:
    1. Component boundaries and responsibilities
    2. Inter-component relationships
    3. Architectural patterns used
    4. Appropriate C4 container assignments
    """
    
    project_context = analyze_project_structure(project_path)
    code_patterns = extract_code_patterns(project_path)
    
    llm_analysis = llm.analyze({
        "project_structure": project_context,
        "code_samples": code_patterns,
        "task": "identify_microservice_components_and_relationships"
    })
    
    return generate_c4_model(llm_analysis)
```



## Project Structure and Context

### Target Project Location
The Avatar project being analyzed is located at:
```
/home/mohamad-khaled-minawe/Desktop/project/Structurizer/avatar-dataspaces-demo
```

### C4 Generator Project Location
The C4 model generator tool is located at:
```
/home/mohamad-khaled-minawe/Downloads/TestProjcet/C4Gentator
```

## How We Started: Project Analysis and Strategy Development

### Initial Project Investigation

Our journey began with a comprehensive analysis of the Avatar project structure to understand the codebase and identify patterns that could be leveraged for automated C4 model generation.

#### 1. Project Structure Analysis

We started by examining the Avatar project directory structure:
```
/home/mohamad-khaled-minawe/Desktop/project/Structurizer/avatar-dataspaces-demo/
├── de.avatar.connector.api/
├── de.avatar.connector.model/
├── de.avatar.connector.isma/
├── de.avatar.connector.other/
├── de.avatar.connector.emf/
├── de.avatar.connector.whiteboard/
└── ...other modules
```

This analysis revealed:
- **Modular OSGi Structure**: Each module follows OSGi bundle conventions
- **Clear Separation of Concerns**: API, Model, Implementations, and Infrastructure
- **Maven-based Build System**: Standard Java project structure
- **EMF Integration**: Eclipse Modeling Framework for data models

#### 2. UML Class Diagram Creation

To understand the relationships between components, we created UML class diagrams that helped identify:


<img src="images/drawio.svg" alt="UML Class Diagram" width="1200" height="800">




**Key Relationships Identified:**
- Implementation relationships (ISMAConnectorImpl → AvatarConnector)
- Usage relationships (Components → EMF Models)
- Containment relationships (ConnectorInfo → ConnectorEndpoint)
- Extension relationships (Interface hierarchies)

#### 3. Build System Analysis

We analyzed how the Avatar project builds to understand:
- **Compilation Process**: Maven compiles OSGi bundles with annotations
- **Bytecode Generation**: Annotations are preserved in compiled .class files
- **Bundle Structure**: Each module creates separate JAR files
- **Dependencies**: Inter-module dependencies through OSGi services

### Convention Identification and Strategy Development

#### OSGi Annotation Conventions

Through code analysis, we identified several key conventions used throughout the Avatar project:

##### 1. **@Component Annotation Convention**
```java
@Component(
    immediate = true,
    property = {
        "connector=isma.himsa",
        "protocol=HL7",
        "version=1.0"
    }
)
public class ISMAConnectorImpl implements AvatarConnector {
    // Implementation
}
```

**Convention Pattern**: 
- `property` array contains key-value pairs
- `connector=<type>` identifies connector implementations
- Additional metadata in property strings

##### 2. **@ProviderType Annotation Convention**
```java
@ProviderType
public interface ConnectorInfo extends EObject {
    String getId();
    String getName();
    // Interface methods
}
```

**Convention Pattern**:
- Used on EMF model interfaces
- Marks service provider interfaces
- Indicates API contracts

##### 3. **Naming Conventions**
```java
// Implementation classes
ISMAConnectorImpl
OtherConnectorImpl

// Factory classes  
EcoreSerializerFactory
AConnectorFactory

// Whiteboard pattern
ConnectorWhiteboard
ConnectorWhiteboardImpl

// EMF Models
ConnectorInfo, ConnectorEndpoint, Parameter
```

**Convention Patterns**:
- `*Impl` suffix for implementations
- `*Factory` suffix for factory classes
- `*Whiteboard*` for OSGi whiteboard pattern
- Descriptive names for EMF model classes

##### 4. **Package Structure Conventions**
```
de.avatar.connector.api        → API definitions
de.avatar.connector.model      → EMF data models  
de.avatar.connector.isma       → ISMA protocol implementation
de.avatar.connector.other      → Other protocol implementations
de.avatar.connector.emf        → EMF infrastructure
de.avatar.connector.whiteboard → OSGi whiteboard services
```

**Convention Pattern**:
- `api` package for interfaces and contracts
- `model` package for data structures
- Protocol-specific packages for implementations
- Infrastructure packages for supporting services

### Strategy Development Based on Conventions

#### Strategy 1: Custom OSGi Component Matcher
Based on the `@Component(property = {"connector=..."})` convention:

```java
new NewComponentStratgy(
    "org.osgi.service.component.annotations.Component",
    "connector",        // Look for "connector" property
    "property"          // In the "property" array
)
```

**Why This Works**:
- Avatar consistently uses `connector=<type>` in property arrays
- Allows filtering of connector implementations from other OSGi components
- Preserves annotation metadata in bytecode

#### Strategy 2: ProviderType Annotation Matcher  
Based on EMF model interface convention:

```java
new AnnotationTypeMatcher("org.osgi.annotation.versioning.ProviderType")
```

**Why This Works**:
- EMF model interfaces consistently use `@ProviderType`
- Distinguishes API contracts from implementations
- Captures data model components

#### Strategy 3: Regex Pattern Matcher
Based on naming and package conventions:

```java
new RegexTypeMatcher("^de\\.avatar\\.connector\\.(emf|whiteboard)\\..*(?:Whiteboard|Serializer|Factory)(?!\\$).*")
```

**Why This Works**:
- Targets specific packages (`emf`, `whiteboard`)
- Matches naming patterns (`Factory`, `Serializer`, `Whiteboard`)
- Excludes inner classes with `(?!\\$)`

#### Strategy 4: Name Suffix Matcher
Based on implementation naming convention:

```java
new NameSuffixTypeMatcher("ConnectorImpl")
```

**Why This Works**:
- Consistently identifies connector implementations
- Simple and reliable pattern matching
- Captures all connector implementations regardless of package




### Compilation Strategy
1. **Source Analysis**: Examined Java source files for annotation patterns
2. **Bytecode Analysis**: Used BCEL to read compiled annotation metadata  
3. **Class Path Analysis**: Identified where compiled classes are located
4. **Dependency Analysis**: Understood inter-module relationships

### Initial Challenges and Solutions

#### Challenge 1: Complex Project Structure
**Problem**: Avatar has multiple Maven modules with interdependencies
**Solution**: Focus on compiled output directories rather than source

#### Challenge 2: Dynamic OSGi Relationships  
**Problem**: Service bindings happen at runtime
**Solution**: Create JSON mapping for explicit relationship definition

#### Challenge 3: EMF Model Complexity
**Problem**: EMF generates many classes automatically
**Solution**: Use `@ProviderType` annotation to identify key interfaces

#### Challenge 4: Annotation Property Parsing
**Problem**: OSGi property arrays need custom parsing logic
**Solution**: Develop `NewComponentStrategy` with BCEL bytecode analysis

### Validation of Our Approach

After developing our strategies based on identified conventions, we validated them by:

1. **Manual Component Counting**: Counted expected components in Avatar project
2. **Strategy Testing**: Ran each strategy individually to verify discovery
3. **Relationship Verification**: Checked that discovered relationships matched UML diagrams
4. **Cross-Validation**: Compared automated discovery with manual analysis








## Component Discovery Strategies in The c4Genrator

The C4 generator implements multiple sophisticated strategies to automatically discover and document components within the Avatar system:

### 1. Custom OSGi Component Strategy (`NewComponentStrategy`)

#### Purpose
This is the primary innovation of the project - a custom TypeMatcher that extends Structurizr's component discovery capabilities to identify OSGi components with specific annotation properties.

#### Implementation Details
```java
public class NewComponentStratgy implements TypeMatcher {
    private final String annotationType;  // Target annotation in JVM format
    private final String propertyName;    // Property to search for (e.g., "connector")
    private final String property;        // Annotation attribute (e.g., "property")
}
```

#### How It Works
1. **Bytecode Analysis**: Uses Apache BCEL (Byte Code Engineering Library) to inspect compiled Java classes
2. **Annotation Matching**: Searches for `@Component` annotations from OSGi
3. **Property Filtering**: Looks for specific properties within annotation arrays
4. **Pattern Matching**: Identifies components with properties like `connector=isma.himsa`

#### Usage Example
```java
new NewComponentStratgy(
    "org.osgi.service.component.annotations.Component",
    "connector",
    "property"
)
```

This discovers OSGi components annotated like:
```java
@Component(property = {"connector=isma.himsa", "service.type=healthcare"})
public class ISMAConnectorImpl implements AvatarConnector {
    // Implementation
}
```

### 2. ProviderType Annotation Strategy

#### Purpose
Discovers components marked with OSGi's `@ProviderType` annotation, typically used for service interfaces and API contracts.

#### Implementation
```java
new AnnotationTypeMatcher("org.osgi.annotation.versioning.ProviderType")
```

#### Target Components
- EMF model interfaces
- Service provider interfaces
- API contract definitions

### 3. Regex Pattern Strategy

#### Purpose
Identifies infrastructure components using naming patterns and package structures.

#### Implementation
```java
new RegexTypeMatcher("^de\\.avatar\\.connector\\.(emf|whiteboard)\\..*(?:Whiteboard|Serializer|Factory)(?!\\$).*")
```

#### Target Components
- Whiteboard pattern implementations
- Serialization services
- Factory components
- Infrastructure utilities

### 4. Name Suffix Strategy

#### Purpose
Discovers connector implementations based on consistent naming conventions.

#### Implementation
```java
new NameSuffixTypeMatcher("ConnectorImpl")
```

#### Target Components
- Protocol-specific connector implementations
- Service implementations
- Adapter patterns

## JSON Configuration System

### The Structurizr Limitation Challenge

One of the key challenges we encountered was that **Structurizr's ComponentFinder cannot automatically detect relationships between components**. While Structurizr excels at discovering components through annotations and patterns, it lacks the capability to understand semantic relationships like "uses", "implements", "extends", or "depends on" that exist between components.



### Our Solution: Custom Component Mapper

To overcome this limitation, we developed a JSON-based component mapper that allows us to **explicitly define relationships in an easier and more maintainable way**.

#### Component Mapping Structure
```json
{
  "container": [
    {
      "connectorModel": {
        "objectMapper": [
          {
            "componentName": "Connector Info",
            "tags": "Info",
            "technology": "EMF Model",
            "description": "Contains metadata about a connector including id, name, provider, version",
            "relations": [
              {
                "target": "Connector Endpoint",
                "type": "uses"
              },
              {
                "target": "Connector Metric",
                "type": "uses"
              }
            ]
          },
          {
            "componentName": "Endpoint Request",
            "tags": "Request",
            "technology": "EMF Model", 
            "description": "Represents a request sent to an endpoint with parameters and timestamp",
            "relations": [
              {
                "target": "Connector Endpoint",
                "type": "uses"
              },
              {
                "target": "Parameter",
                "type": "uses"
              }
            ]
          }
        ]
      }
    }
  ]
}
```

#### How the Component Mapper Works

1. **Discovery Phase**: Structurizr discovers components using our custom strategies
2. **Enrichment Phase**: Our `assignRealtionFromJson()` method matches discovered components with JSON definitions
3. **Relationship Creation**: Explicitly defined relationships are programmatically created between components

```java
public static void assignRealtionFromJson(Container container, Map<String, ComponentDetail> componentMap) {
    for (Component component : container.getComponents()) {
        for (Map.Entry<String, ComponentDetail> entry : componentMap.entrySet()) {
            String keySubstring = entry.getKey();
            ComponentDetail detail = entry.getValue();

            // Match component by name substring
            if (component.getName().contains(keySubstring)) {
                // Apply metadata
                component.setTechnology(detail.getTechnology());
                component.addTags(detail.getTags());
                component.setDescription(detail.getDescription());

                // Create relationships
                List<Relations> relations = detail.getRelations();
                if (relations != null && !relations.isEmpty()) {
                    for (Relations relation : relations) {
                        Component targetComponent = container.getComponentWithName(relation.getTarget());
                        if (targetComponent != null) {
                            component.uses(targetComponent, relation.getType());
                            System.out.println("Added relation from " + component.getName() + 
                                             " to " + targetComponent.getName() + 
                                             " of type " + relation.getType());
                        }
                    }
                }
            }
        }
    }
}
```

### Benefits of Our Component Mapper Approach

#### 1. **Explicit Relationship Definition**
```json
{
  "componentName": "ISMA Connector Impl",
  "relations": [
    {
      "target": "AvatarConnector",
      "type": "implements"
    },
    {
      "target": "Connector Info", 
      "type": "uses"
    },
    {
      "target": "Endpoint Request",
      "type": "creates"
    }
  ]
}
```

#### 2. **Domain-Specific Relationship Types**
We can define healthcare and OSGi-specific relationships:
- `"implements"` - OSGi service implementation
- `"uses"` - Service dependency
- `"creates"` - Factory pattern relationships
- `"responds-to"` - Request/response patterns
- `"provides"` - Service provider relationships

#### 3. **Easy Maintenance and Updates**
- **Version Control**: Relationship changes are tracked in Git
- **Documentation as Code**: JSON serves as living documentation
- **Validation**: JSON schema ensures relationship integrity




### Alternative Approaches We Considered


#### 1. **Annotation-Based Relationships**
**What it would involve**: Custom annotations in source code to define relationships

```java
@ComponentRelation(target = "ConnectorEndpoint", type = "uses")
public class ConnectorInfo {
    // Implementation
}
```

**Why we rejected it**:
- Requires modifying Avatar project source code
- Couples documentation with implementation
- Difficult to update without code changes
- Not suitable for third-party libraries


#### 4. **Our JSON Mapper Approach** 

- **Non-invasive**: No changes to Avatar source code required
- **Maintainable**: Easy to update relationships without compilation  


## Discovery Process Workflow

### 1. Initialization Phase
```java
// Load configuration
File json = new File("src/main/java/org/example/json/componentMapper.json");
ContainerConfig config = ContainerConfig.loadFromFile(json);
Map<String, ContainerDetail> allContainers = config.getContainerMap();
```

### 2. Component Scanning Phase
```java
// Scan different containers with appropriate strategies
tryScanningByOSGiFindAllModel(connectorModel, path, componentMap);
findInfrastructureComponents(infrastructure, path2, infrastructureMap);
tryScanningByOSGiFindAllModelNew(implementations, path2, connectorMap);
```

### 3. Enrichment Phase
```java
// Apply metadata from JSON configuration
assignRealtionFromJson(container, componentMap);
```

### 4. Model Generation Phase
```java
// Create C4 views and export
ComponentView modelView = views.createComponentView(connectorModel, "model-components", "Connector Model Components");
new JsonWriter(true).write(workspace, writer);
```

## Getting Started

### Prerequisites
- Java 22 or higher
- Maven 3.8+
- Access to the Avatar project source code

### Dependencies
```xml
<dependencies>
    <dependency>
        <groupId>com.structurizr</groupId>
        <artifactId>structurizr-core</artifactId>
        <version>4.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.structurizr</groupId>
        <artifactId>structurizr-component</artifactId>
        <version>4.0.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.bcel</groupId>
        <artifactId>bcel</artifactId>
        <version>6.10.0</version>
    </dependency>
    <dependency>
        <groupId>org.osgi</groupId>
        <artifactId>org.osgi.service.component.annotations</artifactId>
        <version>1.5.1</version>
    </dependency>
</dependencies>
```

### Running the Generator

1. **Build the Avatar Project** (if not already built):
```bash
cd /home/mohamad-khaled-minawe/Desktop/project/Structurizer/avatar-dataspaces-demo
mvn clean compile
```

2. **Update Path Configuration** in `AvatarC4ModelGenerator.java`:
```java
String basePathModel = "/home/mohamad-khaled-minawe/Desktop/project/Structurizer/avatar-dataspaces-demo/de.avatar.connector.model";
String basePathProject = "/home/mohamad-khaled-minawe/Desktop/project/Structurizer/avatar-dataspaces-demo";
```

3. **Run the Generator**:
```bash
cd /home/mohamad-khaled-minawe/Downloads/TestProjcet/C4Gentator
mvn clean compile exec:java -Dexec.mainClass="org.example.c4.AvatarC4ModelGenerator"
```

4. **Output**: The generator creates `avatar-c4-model.json` containing the complete C4 model.

### Configuration Customization

#### Adding New Component Types
1. Update `componentMapper.json` with new component definitions
2. Implement custom TypeMatcher if needed
3. Add new scanning strategy to `tryScanningForComponents()`

#### Extending Discovery Strategies
```java
// Example: Custom annotation strategy
ComponentFinder finder = new ComponentFinderBuilder()
    .forContainer(container)
    .fromClasses(path)
    .withStrategy(
        new ComponentFinderStrategyBuilder()
            .matchedBy(new CustomTypeMatcher("your.annotation.Type"))
            .withTechnology("Your Technology")
            .build()
    )
    .build();
```




