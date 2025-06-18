# Configurable Strategy System Documentation

## Overview

The C4 Generator now supports a flexible, JSON-based configuration system for component discovery strategies. This allows users to easily customize how components are discovered without modifying code.

## Configuration Structure

### Strategy Configuration File (`strategyConfig.json`)

The main configuration file defines:
- **Strategies**: List of component discovery strategies
- **Global Config**: Settings that apply to all strategies

### Strategy Types

#### 1. ANNOTATION Strategy
Discovers components marked with specific annotations.

```json
{
  "name": "Provider Type Strategy",
  "type": "ANNOTATION",
  "config": {
    "annotationType": "org.osgi.annotation.versioning.ProviderType"
  },
  "containerMapping": "connectorModel"

}
```

#### 2. REGEX Strategy
Discovers components using regular expression patterns.

```json
{
  "name": "Infrastructure Components Strategy",
  "type": "REGEX",
  "description": "Discovers infrastructure components using package and naming patterns",
  "config": {
    "pattern": "^de\\.avatar\\.connector\\.(emf|whiteboard)\\..*(?:Whiteboard|Serializer|Factory)(?!\\$).*"
  },
  "containerMapping": "connectorInfrastructure"
}
```

#### 3. NAME_SUFFIX Strategy
Discovers components based on naming conventions.

```json
{
  "name": "Connector Implementation Strategy",
  "type": "NAME_SUFFIX",
  "description": "Discovers connector implementations by naming convention",
  "config": {
    "suffix": "ConnectorImpl"
  },
  "containerMapping": "connectorImplementations"
}
```

#### 4. CUSTOM_ANNOTATION Strategy
Discovers components using custom annotation property matching.

```json
{
  "name": "OSGi Component Strategy",
  "type": "CUSTOM_ANNOTATION",
  "description": "Discovers OSGi components with specific connector properties",
  "config": {
    "annotationType": "org.osgi.service.component.annotations.Component",
    "propertyName": "connector",
    "annotationProperty": "property"
  },
  "containerMapping": "connectorImplementations"
}
```

## How to Use

### 1. Create Your Strategy Configuration

Create or modify `src/main/java/org/example/json/strategyConfig.json`:

```json
{
  "strategies": [
    {
      "name": "My Custom Strategy",
      "type": "REGEX",
      "description": "Discovers my custom components",
      "config": {
        "pattern": ".*MyComponent.*"
      },
      "containerMapping": "myContainer",
      "technology": "Java",
      "enabled": true
    }
  ],
  "globalConfig": {
    "excludeInnerClasses": true,
    "excludeTestClasses": true,
    "basePaths": {
      "myContainer": "/path/to/my/project"
    }
  }
}
```

### 2. Update Path Configuration

Modify the `basePaths` in `globalConfig` to point to your project directories:

```json

"basePaths":{
  "connectorModel": "/your/project/path/model",
  "connectorImplementations": "/your/project/path",
  "connectorInfrastructure": "/your/project/path",
  "connectorApi": "/your/project/path/api"
}
```

### 3. Run the Generator

The generator will automatically load and apply your configured strategies:

```bash
mvn clean compile exec:java -Dexec.mainClass="org.example.c4.AvatarC4ModelGenerator"
```

## Configuration Examples

### Discovering Spring Components

```json
{
  "name": "Spring Component Strategy",
  "type": "ANNOTATION",
  "config": {
    "annotationType": "org.springframework.stereotype.Component"
  },
  "containerMapping": "springComponents",
  "technology": "Spring"
}
```

### Discovering REST Controllers

```json
{
  "name": "REST Controller Strategy",
  "type": "ANNOTATION",
  "config": {
    "annotationType": "org.springframework.web.bind.annotation.RestController"
  },
  "containerMapping": "webLayer",
  "technology": "Spring MVC"
}
```

### Discovering Service Classes

```json
{
  "name": "Service Implementation Strategy",
  "type": "NAME_SUFFIX",
  "config": {
    "suffix": "ServiceImpl"
  },
  "containerMapping": "serviceLayer",
  "technology": "Java"
}
```

### Discovering Repository Classes

```json
{
  "name": "Repository Strategy",
  "type": "REGEX",
  "config": {
    "pattern": ".*Repository$"
  },
  "containerMapping": "dataLayer",
  "technology": "JPA"
}
```

## Benefits

### 1. **No Code Changes Required**
- Modify component discovery without touching Java code
- Version control your discovery strategies
- Easy to maintain and update

### 2. **Project Agnostic**
- Configure for any Java project structure
- Support different frameworks and conventions
- Reusable across projects

### 3. **Flexible Matching**
- Combine multiple strategy types
- Fine-tune discovery with regex patterns
- Custom annotation property matching

### 4. **Easy Testing**
- Enable/disable strategies individually
- Test different configurations quickly
- Validate strategy effectiveness

## Advanced Features

### Strategy Validation
The system automatically validates strategy configurations and provides clear error messages for missing or invalid parameters.

### Component Enrichment
Discovered components are automatically enriched with:
- Default descriptions based on naming patterns
- Appropriate tags based on strategy type
- Technology assignments from configuration

### Path Management
Global configuration manages base paths for different containers, making it easy to point to different project structures.

### Component Filtering
Global settings allow filtering out inner classes, test classes, and other unwanted components.

This configurable system makes the C4 generator truly flexible and reusable across different projects and architectural patterns.
