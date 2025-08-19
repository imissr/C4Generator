b# C4 Generator - Automated C4 Model Documentation

## Overview

The **C4 Generator** is a powerful tool designed to make documentation easier while building systems or microservices by automatically generating C4 diagrams for your projects. It leverages the Structurizr library to create comprehensive architectural documentation with minimal manual effort.

## Project Objectives

- **Automated Documentation**: Generate C4 model diagrams automatically during system development
- **Component Discovery**: Intelligently scan and detect components using configurable strategies
- **CI/CD Integration**: Support continuous integration pipelines to scan projects from target repositories
- **Flexible Configuration**: JSON-based configuration for different scanning strategies and project structures

## Architecture Overview

The C4 Generator follows a layered approach:

- **System Layer**: User-defined software systems and external dependencies
- **Container Layer**: User-defined containers within software systems
- **Component Layer**: Automatically discovered components using configurable scanning strategies

## Key Features

### 1. Multi-Strategy Component Scanning
The generator supports various strategies to discover components:
- **Annotation-based**: Scan for specific Java annotations (e.g., `@RestController`, `@Service`, `@Repository`)
- **Regex-based**: Pattern matching for class names (e.g., classes ending with "Mapper", "DTO")
- **Custom strategies**: Extensible framework for additional scanning approaches

### 2. JSON Configuration System
Two main configuration files control the generation process:

#### C4 Model Configuration (`c4ModelConfig.json`)
Defines the overall structure of your target project:
- Workspace metadata
- Persons (actors/users)
- Software systems
- Containers
- Component mappings and relationships

#### Strategy Configuration (`strategyConfig.json`)
Defines how components are discovered:
- Enabled/disabled strategies
- Annotation types to scan
- Regex patterns for class matching
- Container mappings for discovered components

### 3. CI/CD Pipeline Support
- **Change Detection**: Only regenerate documentation when architectural changes occur
- **Multiple Modes**: Support for different CI/CD scenarios
- **Baseline Creation**: Establish initial architectural snapshots
- **Performance Optimized**: Quick scanning without full model generation

## CI/CD Pipeline Integration

The C4 Generator includes comprehensive CI/CD support for automatically scanning target repositories and generating documentation when architectural changes occur. This system uses GitHub Actions workflows to enable cross-repository scanning.

### Architecture Overview

The CI/CD system consists of two parts:

1. **Target Project Workflow**: Triggers C4 scanning when code changes
2. **C4 Generator CI Workflow**: Receives scan requests and processes target repositories

### Setting Up CI/CD Integration

#### Step 1: Configure Target Project

Add this workflow to your target project at `.github/workflows/trigger-c4-scan.yml`:

```yaml
name: Trigger C4‑Scan

on:
  push:
    branches:
      - '**'           # or restrict to main/master if you prefer

jobs:
  dispatch:
    name: Dispatch to C4 CI
    runs-on: ubuntu-latest

    steps:
      - name: Send repository_dispatch to C4Generator
        uses: peter-evans/repository-dispatch@v2
        with:
          # ← Must be a PAT (NOT GITHUB_TOKEN) with repo scope, stored as a secret
          token: ${{ secrets.C4_CI_TOKEN }}

          # ← Exact owner/repo of your C4 CI workflow
          repository: imissr/C4Generator

          # ← Must match the dispatch type your CI listens for
          event-type: c4-scan-request

          # ← Passing along which repo/ref/sha triggered this
          client-payload: |
            {
              "source_repo": "${{ github.repository }}",
              "ref":         "${{ github.ref }}",
              "sha":         "${{ github.sha }}"
            }
```

#### Step 2: Configure Secrets

In your target project, add the following secrets:

- `C4_CI_TOKEN`: Personal Access Token with `repo` scope for triggering the C4 Generator workflow

#### Step 3: C4 Generator CI Workflow

The C4 Generator repository includes a comprehensive CI workflow (`c4-architecture-ci.yml`) that:

- **Receives Dispatch Events**: Listens for `c4-scan-request` events from target repositories
- **Clones Target Repository**: Downloads and compiles the source repository
- **Performs Component Scanning**: Uses configurable strategies to discover components
- **Detects Changes**: Compares with previous snapshots to identify architectural changes
- **Generates Documentation**: Creates C4 models only when changes are detected
- **Manages Snapshots**: Maintains historical snapshots for change detection

### CI/CD Operational Modes

#### 1. Baseline Mode
Creates initial architectural snapshot for new repositories:
```bash
java org.example.c4.C4ModelGeneratorCI baseline
```

#### 2. Change Detection Mode
Detects architectural changes and exits with appropriate status codes:
- Exit 0: No changes detected
- Exit 1: Changes detected (triggers documentation generation)
- Exit 2: Error occurred

```bash
java org.example.c4.C4ModelGeneratorCI change-detect
```

#### 3. Generate If Changed Mode
Performs full C4 model generation only when changes are detected:
```bash
java org.example.c4.C4ModelGeneratorCI generate-if-changed
```

#### 4. Serialize Only Mode
Creates component snapshots without full model generation (for performance):
```bash
java org.example.c4.C4ModelGeneratorCI serialize-only
```

### Workflow Features

#### Smart Change Detection
- **Component-Level Tracking**: Monitors individual components for changes
- **Relationship Mapping**: Detects changes in component relationships
- **Performance Optimized**: Avoids full generation when no changes occur
- **Historical Snapshots**: Maintains component snapshots across builds

#### Multi-Repository Support
- **Repository Isolation**: Separate snapshots for each target repository
- **Concurrent Processing**: Handle multiple repository scan requests
- **Flexible Configuration**: Per-repository configuration support

#### Artifact Management
- **Snapshot Storage**: Maintains component snapshots in dedicated branch
- **Artifact Upload**: Generated models available as workflow artifacts
- **Retention Policies**: Configurable retention for generated artifacts

### Configuration for CI/CD

The CI/CD system automatically patches the strategy configuration to point to the correct target project paths:

```json
{
  "globalConfig": {
    "basePaths": {
      "webApplication": "scanned-project/target/classes",
      "securityLayer": "scanned-project/target/classes"
    }
  }
}
```

### Benefits of CI/CD Integration

- **Automated Documentation**: No manual intervention required
- **Always Current**: Documentation updates with every architectural change
- **Performance Efficient**: Only processes when changes are detected
- **Multi-Project Support**: Single C4 Generator instance can serve multiple projects
- **Historical Tracking**: Maintains architectural evolution history
- **Team Collaboration**: Automatic updates keep entire team informed

### Prerequisites

- GitHub repository with Actions enabled
- Java 22+ runtime environment
- Maven build system
- Personal Access Token with repository scope
- Target project must be compilable with Maven

This CI/CD integration makes architectural documentation truly automated and ensures your C4 models are always up-to-date with your codebase.

## Configuration Files

### C4 Model Configuration Structure

The C4 model configuration requires **manual setup** of the high-level architecture elements. You must define these sections in `c4ModelConfig.json`:

```json
{
  "workspace": {
    "name": "Your System Name",
    "description": "System description"
  },
  "persons": [
    {
      "name": "Customer",
      "description": "System user who interacts with the application",
      "relations": [
        {
          "target": "Your System Name",
          "type": "Uses"
        }
      ]
    }
  ],
  "softwareSystems": [
    {
      "name": "Your System Name",
      "description": "Main system description",
      "relations": [
        {
          "target": "External Database",
          "type": "Stores data in"
        }
      ]
    }
  ],
  "containers": [
    {
      "name": "Web Application",
      "description": "Spring Boot REST API",
      "technology": "Spring Boot, Java 21",
      "softwareSystemName": "Your System Name"
    }
  ],
  "container": [
    {
      "webApplication": {
        "objectMapper": [...]
      }
    }
  ]
}
```

### Required Manual Configuration

#### ⚠️ **Critical: These sections must be manually configured**

1. **Workspace**: Define your project's name and description
2. **Persons**: Define all actors/users that interact with your system
3. **Software Systems**: Define your main system and any external systems
4. **Containers**: Define the logical containers within your software systems

> **Note**: Currently, the workspace, persons, and software systems must be manually defined in the configuration file. This requirement will be automated in future versions, but for now, these high-level architectural elements require manual specification to create a complete C4 model.

#### ✅ **Automatically Discovered**

- **Components**: Automatically discovered using configured strategies
- **Component Relationships**: Mapped from JSON configuration and discovered dependencies

### Strategy Configuration Structure

```json
{
  "strategies": [
    {
      "name": "Spring MVC Controllers",
      "type": "ANNOTATION",
      "config": {
        "annotationType": "org.springframework.web.bind.annotation.RestController"
      },
      "containerMapping": "webApplication",
      "enabled": true
    }
  ],
  "globalConfig": {
    "excludeInnerClasses": true,
    "excludeTestClasses": true,
    "basePaths": {
      "webApplication": "/path/to/target/classes"
    }
  }
}
```

## Important Configuration Rules

### Container Mapping Consistency
**Critical**: The `containerMapping` field in the strategy configuration must match the container names defined in the C4 model configuration.

**Example**:
- In `c4ModelConfig.json`: Container name is `"Web Application"`
- In `strategyConfig.json`: `containerMapping` should be `"webApplication"` (camelCase version)

The system uses automated matching strategies to map container names to configuration keys.

## Usage

### Basic Generation
```bash
java -cp target/classes org.example.c4.C4ModelGenerator
```

### CI/CD Modes
```bash
# Detect changes only
java org.example.c4.C4ModelGeneratorCI change-detect

# Generate only if changes detected
java org.example.c4.C4ModelGeneratorCI generate-if-changed

# Create baseline snapshot
java org.example.c4.C4ModelGeneratorCI baseline

# Serialize components only
java org.example.c4.C4ModelGeneratorCI serialize-only
```

## Project Structure

```
c4genrator/
├── src/main/java/org/example/
│   ├── c4/                          # Main generators
│   │   ├── C4ModelGenerator.java    # Primary generation logic
│   │   └── C4ModelGeneratorCI.java  # CI/CD optimized version
│   ├── config/                      # Configuration management
│   ├── json/                        # Configuration files
│   │   ├── c4ModelConfig.json       # C4 model structure
│   │   └── strategyConfig.json      # Component scanning strategies
│   ├── model/                       # Data models
│   ├── service/                     # Core services
│   │   ├── ComponentChangeDetector.java
│   │   ├── ComponentSerializationService.java
│   │   └── ConfigurableComponentScanner.java
│   └── strategies/                  # Scanning strategy implementations
├── discovered-components/           # Generated component snapshots
└── target/                         # Compiled classes
```

## Supported Technologies

- **Primary Language**: Java
- **Framework Support**: Spring Boot, Spring MVC, Spring Data JPA
- **Annotation Detection**: Jakarta Persistence, Spring annotations
- **Build Tools**: Maven
- **CI/CD**: Any pipeline supporting Java execution

## Getting Started

1. **Configure your project structure** in `c4ModelConfig.json`:
   - Define your software systems
   - Specify containers
   - Map component relationships

2. **Set up scanning strategies** in `strategyConfig.json`:
   - Enable relevant annotation-based strategies
   - Configure regex patterns for custom component types
   - Ensure `containerMapping` matches your container names

3. **Run the generator**:
   ```bash
   mvn compile
   java -cp target/classes org.example.c4.C4ModelGenerator
   ```

4. **Integrate with CI/CD**:
   - Add change detection to your pipeline
   - Configure baseline creation on main branch updates
   - Set up automated documentation updates

## Benefits

- **Reduced Documentation Overhead**: Automatic component discovery eliminates manual diagram maintenance
- **Always Up-to-Date**: Documentation stays synchronized with code changes
- **Team Productivity**: Developers focus on coding while documentation generates automatically
- **Architectural Visibility**: Clear visualization of system components and relationships
- **CI/CD Optimized**: Efficient change detection prevents unnecessary processing

## Component Discovery Strategies

The C4 Generator supports multiple configurable strategies for discovering components in your codebase. All strategies are defined in `strategyConfig.json` and can be enabled/disabled as needed.

### Available Strategy Types

#### 1. ANNOTATION Strategy
Discovers components marked with specific Java annotations:
```json
{
  "name": "Spring Controllers",
  "type": "ANNOTATION",
  "config": {
    "annotationType": "org.springframework.web.bind.annotation.RestController"
  },
  "containerMapping": "webApplication",
  "enabled": true
}
```

#### 2. REGEX Strategy
Uses regular expression patterns to match class names:
```json
{
  "name": "DTO Classes",
  "type": "REGEX",
  "config": {
    "pattern": ".*DTO$"
  },
  "containerMapping": "webApplication",
  "enabled": true
}
```

#### 3. NAME_SUFFIX Strategy
Matches classes based on naming conventions:
```json
{
  "name": "Service Implementations",
  "type": "NAME_SUFFIX",
  "config": {
    "suffix": "ServiceImpl"
  },
  "containerMapping": "serviceLayer"
}
```

#### 4. CUSTOM_ANNOTATION Strategy
Advanced annotation matching with property validation:
```json
{
  "name": "OSGi Components",
  "type": "CUSTOM_ANNOTATION",
  "config": {
    "annotationType": "org.osgi.service.component.annotations.Component",
    "propertyName": "connector",
    "annotationProperty": "property"
  },
  "containerMapping": "connectorImplementations"
}
```

### Strategy Configuration Features

- **Enable/Disable**: Individual strategies can be turned on/off
- **Container Mapping**: Each strategy maps discovered components to specific containers
- **Technology Tags**: Automatic technology labeling based on discovery method
- **Component Filtering**: Global options to exclude inner classes, test classes, etc.
- **Path Management**: Configurable base paths for different containers

### Example Strategy Configuration
```json
{
  "strategies": [
    {
      "name": "Spring MVC Controllers",
      "type": "ANNOTATION",
      "config": {
        "annotationType": "org.springframework.web.bind.annotation.RestController"
      },
      "containerMapping": "webApplication",
      "enabled": true
    }
  ],
  "globalConfig": {
    "excludeInnerClasses": true,
    "excludeTestClasses": true,
    "basePaths": {
      "webApplication": "C:\\path\\to\\target\\classes"
    }
  }
}
```

> **📖 For detailed strategy configuration:** See `CONFIGURABLE_STRATEGY_GUIDE.md` for comprehensive examples, advanced configuration options, and best practices for different project types and frameworks.

## Contributing

The project uses a modular, strategy-based architecture that makes it easy to extend:
- Add new component scanning strategies in the `strategies` package
- Extend configuration models for additional metadata
- Implement custom relationship detection algorithms

## Future Enhancements

- Support for additional programming languages
- Integration with more frameworks and libraries
- Enhanced relationship detection algorithms
- Web-based configuration interface
- Integration with popular documentation platforms


# JSON Schema Validation for C4 Model Configuration

This document explains how to use JSON schema validation to ensure your C4 model configuration files are correctly structured before processing.

## Overview

The C4 Model Generator now includes comprehensive JSON schema validation that:
- ✅ Validates structure and data types
- ✅ Ensures required fields are present
- ✅ Validates field formats and patterns
- ✅ Provides detailed error messages
- ✅ Works automatically with your existing configurations

## Features

### Automatic Validation
The generator automatically validates your configuration when loading:
```java
// Validation is enabled by default
C4ModelConfig config = C4ModelConfig.loadFromFile(jsonFile);

// Or explicitly enable/disable validation
C4ModelConfig config = C4ModelConfig.loadFromFile(jsonFile, true);
```

### Schema Features
The JSON schema validates:

1. **Workspace Configuration**
   - Required: `name`, `description`
   - Name must match pattern: `^[A-Za-z][A-Za-z0-9 _-]*$`
   - Description: 1-500 characters

2. **Persons Array**
   - Each person requires: `name`, `description`
   - Optional: `relations` array
   - Name pattern validation for consistency

3. **Software Systems Array**
   - At least one system required
   - Each system requires: `name`, `description`
   - Optional: `relations` array

4. **Containers Array**
   - At least one container required
   - Each container requires: `name`, `description`, `technology`
   - Technology examples: "Java/Spring Boot", "Node.js/Express", etc.

5. **Container Component Mappings**
   - Dynamic container names as keys
   - Each component requires: `componentName`, `tags`, `technology`, `description`
   - Optional: `relations` array

6. **Relations Validation**
   - Each relation requires: `target`, `type`
   - Predefined relationship types or custom ones
   - Target name pattern validation

## Example Valid Configuration

```json
{
  "workspace": {
    "name": "My Project C4 Model",
    "description": "Architecture model for my application"
  },
  "persons": [
    {
      "name": "End User",
      "description": "Primary user of the system",
      "relations": [
        {
          "target": "My System",
          "type": "uses"
        }
      ]
    }
  ],
  "softwareSystems": [
    {
      "name": "My System",
      "description": "Core application system",
      "relations": []
    }
  ],
  "containers": [
    {
      "name": "Web Application",
      "description": "React-based frontend",
      "technology": "React/TypeScript",
      "relations": [
        {
          "target": "API Gateway",
          "type": "calls"
        }
      ]
    },
    {
      "name": "API Gateway",
      "description": "REST API backend",
      "technology": "Node.js/Express",
      "relations": []
    }
  ],
  "container": [
    {
      "webApp": {
        "objectMapper": [
          {
            "componentName": "User Interface",
            "tags": "Frontend",
            "technology": "React",
            "description": "Main user interface components"
          }
        ]
      }
    }
  ]
}
```

## Common Validation Errors

### 1. Missing Required Fields
```
❌ Error: $.workspace.description: is missing but it is required
```
**Fix:** Add the missing required field

### 2. Invalid Data Types
```
❌ Error: $.persons: string found, array expected
```
**Fix:** Ensure the field is the correct data type (array, object, string, etc.)

### 3. Pattern Validation Failures
```
❌ Error: $.persons[0].name: does not match the regex pattern ^[A-Za-z][A-Za-z0-9 _-]*$
```
**Fix:** Use names that start with a letter and contain only letters, numbers, spaces, underscores, or hyphens

### 4. Length Violations
```
❌ Error: $.workspace.description: may only be 500 characters long
```
**Fix:** Shorten the text to meet the length requirement

### 5. Empty Required Arrays
```
❌ Error: $.containers: must have a minimum of 1 items
```
**Fix:** Add at least one item to required arrays

## Schema File Location

The schema file (`c4ModelConfigSchema.json`) should be:
1. In the same directory as your configuration file, OR
2. Available in the classpath

The generator will automatically find and use the schema for validation.

## Disabling Validation

If you need to disable validation temporarily:
```java
C4ModelConfig config = C4ModelConfig.loadFromFile(jsonFile, false);
```

## Testing Your Configuration

Run the validation test to check your configuration:
```bash
java org.example.test.JsonSchemaValidationTest
```

## Benefits

✅ **Early Error Detection** - Catch configuration errors before processing  
✅ **Detailed Error Messages** - Know exactly what's wrong and where  
✅ **Consistency Enforcement** - Ensure all configurations follow the same structure  
✅ **Documentation** - Schema serves as documentation for the configuration format  
✅ **IDE Support** - Many IDEs can use the schema for auto-completion and validation

## IDE Integration

To enable auto-completion and validation in your IDE:

### VS Code
Add this to your `c4ModelConfig.json`:
```json
{
  "$schema": "./c4ModelConfigSchema.json",
  // ... rest of your configuration
}
```

### IntelliJ IDEA
The IDE will automatically detect and use the schema if it's in the same directory.

## Troubleshooting

**Q: Validation is being skipped**  
A: Ensure the schema file is in the correct location and readable

**Q: Getting unexpected validation errors**  
A: Check that your JSON syntax is valid and all required fields are present

**Q: Want to add custom relationship types**  
A: The schema includes common relationship types but allows custom ones too

---

**Ready to validate your C4 configurations? The schema will help ensure your architecture models are always correctly structured! ✨**
