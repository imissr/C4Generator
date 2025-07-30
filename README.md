# C4Generator Documentation

## Overview

**C4Generator** is a configurable system for generating C4 model architectural documentation for Java projects. Its primary goal is to automate the discovery of software components, their relationships, and generate visualizations and documentation in formats compatible with tools like Structurizr. The tool is highly adaptable, supporting any Java project structure, and leverages configuration-driven strategies defined in JSON files to control how components and relationships are discovered.

### Key Features

- **No Code Changes Required:** Component discovery strategies are externalized to JSON files, so you can change how components are found without modifying Java code.
- **Project Agnostic:** Works with any Java codebase, including Spring, OSGi, microservices, and traditional enterprise applications.
- **Flexible Matching:** Supports multiple discovery strategies, regex patterns, and custom annotation matching.
- **Easy Testing:** Enable or disable strategies individually for rapid experimentation.
- **Automated Relationship Mapping:** Discovers and documents dependencies between components.
- **JSON Schema Validation:** Ensures your configuration files are valid and provides helpful error messages.
- **Support for CI/CD:** Includes tooling for detecting changes in components to automate documentation regeneration in pipelines.

## How to Use

### 1. Configuration

All discovery logic is defined in JSON configuration files (typically found in a `json` folder). These files describe:

- The overall workspace (name, description)
- Persons and software systems (and their relationships)
- Containers (logical architectural groupings)
- Discovery strategies for finding components in source code
- Filtering and enrichment options

#### Example Workflow

1. **Prepare Configuration:** Edit or create JSON files describing your architecture and discovery strategies.
2. **Run the Generator:** Execute the main Java entry point (e.g., `C4ModelGenerator` or `C4ModelGeneratorCI`) and provide the path to your main config file (e.g., `src/main/java/org/example/json/c4ModelConfig.json`).
3. **Review Output:** The tool will produce a Structurizr-compatible JSON model and/or textual reports about detected changes.
4. **Integrate with CI/CD:** Use the CI runner mode to automate documentation updates when code or architecture changes.

### 2. Execution Modes

- **Standard Generation:** Produces full C4 documentation.
- **Quick Component Scan:** For CI pipelines, scans for component changes without full documentation generation.
- **Change Detection:** Detects architectural changes and can cause CI/CD to regenerate documentation.
- **Baseline:** Creates a baseline snapshot of the current architecture for comparison.

## JSON Folder and Its Files

The `json` folder contains all the configuration files that define how C4Generator operates. Each file typically serves a specific purpose:

### Main Configuration File (e.g., `c4ModelConfig.json`)
- **Purpose:** Defines the overall model, including workspace metadata, persons, software systems, containers, and relationships.
- **Contents:**
  - `workspace`: Name and description of the architecture model.
  - `persons`: Actors/users interacting with the system.
  - `softwareSystems`: Logical systems and their interactions.
  - `containers`: Architectural containers (e.g., WebApp, Database).
  - `relations`: Relationships between persons, systems, and containers.

#### Example Snippet
```json
{
  "workspace": {
    "name": "My Project C4 Model",
    "description": "Architecture model for my application"
  },
  "persons": [
    { "name": "End User", "description": "Primary user", "relations": [...] }
  ],
  "softwareSystems": [
    { "name": "My System", "description": "Core system", "relations": [] }
  ],
  "containers": [
    { "name": "Web Application", "description": "React frontend", "technology": "React/TypeScript", "relations": [...] }
  ]
}
```

### Strategy Configuration Files (e.g., `discoveryStrategies.json`)
- **Purpose:** Specify how components are discovered within each container.
- **Contents:**
  - List of strategies for each container (by name).
  - Strategy types (annotation-based, regex, etc.).
  - Filters to include/exclude certain classes/files.
  - Enrichment options (tags, descriptions).

### Schema Validation File (optional)
- **Purpose:** Provides a JSON schema against which config files are validated.
- **Contents:** Schema definitions for structure and types.

### Component Snapshot/History Files (optional, for CI/CD)
- **Purpose:** Track snapshots of discovered components for change detection.
- **Contents:** Lists of components, timestamps, and change events.

## Advanced Features

- **Strategy Validation:** Automatic validation of all strategy config files, with clear error reporting.
- **Component Enrichment:** Auto-generates descriptions, tags, and technology labels for components based on config and naming patterns.
- **Component Filtering:** Configurable options to exclude test/inner classes or unwanted files.
- **Path Management:** Flexible base path definitions for containers, supporting any project layout.
- **Error Resilience:** Discovery continues even if some strategies fail, with errors logged for review.

## References

For further details, see:
- `CONFIGURABLE_STRATEGY_GUIDE.md` for in-depth strategy configuration.
- `JSON_SCHEMA_VALIDATION_GUIDE.md` for configuration schema and validation.
- Java source code (e.g., `C4ModelGenerator.java`, `ConfigurableComponentScanner.java`) for implementation details and usage examples.

---

**In summary:**  
Edit the JSON files in the `json` folder to match your architecture and discovery preferences, then run the generator. Each JSON file has a clear role (model definition, discovery strategy, schema, or snapshot), and all are validated and processed by C4Generator to create up-to-date, accurate C4 documentation for your Java system.
