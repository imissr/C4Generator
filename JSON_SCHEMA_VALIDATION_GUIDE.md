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
