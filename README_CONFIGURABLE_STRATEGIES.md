# C4 Generator - Configurable Strategy System

## 🎯 Overview

The C4 Generator now features a powerful, JSON-based configuration system that allows you to customize component discovery strategies without modifying code. This makes the tool **project-agnostic** and easily adaptable to different architectural patterns and frameworks.

## 🚀 Quick Start

### 1. Configure Your Strategies

Edit `src/main/java/org/example/json/strategyConfig.json`:

```json
{
  "strategies": [
    {
      "name": "My Components",
      "type": "ANNOTATION",
      "config": {
        "annotationType": "com.mycompany.MyAnnotation"
      },
      "containerMapping": "myContainer",
      "technology": "Java"
    }
  ],
  "globalConfig": {
    "basePaths": {
      "myContainer": "/path/to/your/project"
    }
  }
}
```

### 2. Run the Generator

```bash
java -cp "target/classes:lib/*" org.example.c4.AvatarC4ModelGenerator
```

### 3. View Your C4 Model

The generated `avatar-c4-model.json` can be imported into [Structurizr](https://structurizr.com/) for visualization.

## 📋 Strategy Types

### 🏷️ ANNOTATION Strategy
Discovers components marked with specific annotations.

**Use Cases:**
- Spring components (`@Component`, `@Service`, `@Repository`)
- JAX-RS endpoints (`@Path`, `@RestController`)
- JPA entities (`@Entity`)
- Custom business annotations

**Configuration:**
```json
{
  "type": "ANNOTATION",
  "config": {
    "annotationType": "org.springframework.stereotype.Service"
  }
}
```

### 🔍 REGEX Strategy
Discovers components using regular expression patterns.

**Use Cases:**
- Package-based filtering
- Naming convention matching
- Complex architectural patterns

**Configuration:**
```json
{
  "type": "REGEX",
  "config": {
    "pattern": "com\\.mycompany\\..*(?:Service|Repository)$"
  }
}
```

### 📝 NAME_SUFFIX Strategy
Discovers components based on naming conventions.

**Use Cases:**
- Implementation classes (`*Impl`)
- Factory classes (`*Factory`)
- Handler classes (`*Handler`)

**Configuration:**
```json
{
  "type": "NAME_SUFFIX",
  "config": {
    "suffix": "ServiceImpl"
  }
}
```

### ⚙️ CUSTOM_ANNOTATION Strategy
Discovers components using custom annotation property matching.

**Use Cases:**
- OSGi components with properties
- Custom framework annotations
- Feature flags in annotations

**Configuration:**
```json
{
  "type": "CUSTOM_ANNOTATION",
  "config": {
    "annotationType": "org.osgi.service.component.annotations.Component",
    "propertyName": "service",
    "annotationProperty": "property"
  }
}
```

## 📂 Project Examples

### Spring Boot Application

<details>
<summary>Click to expand Spring Boot configuration</summary>

```json
{
  "strategies": [
    {
      "name": "REST Controllers",
      "type": "ANNOTATION",
      "config": {
        "annotationType": "org.springframework.web.bind.annotation.RestController"
      },
      "containerMapping": "webLayer",
      "technology": "Spring MVC"
    },
    {
      "name": "Service Layer",
      "type": "ANNOTATION",
      "config": {
        "annotationType": "org.springframework.stereotype.Service"
      },
      "containerMapping": "businessLayer",
      "technology": "Spring Service"
    },
    {
      "name": "Data Repositories",
      "type": "ANNOTATION",
      "config": {
        "annotationType": "org.springframework.stereotype.Repository"
      },
      "containerMapping": "dataLayer",
      "technology": "Spring Data"
    }
  ],
  "globalConfig": {
    "basePaths": {
      "webLayer": "/path/to/spring-project/src/main/java",
      "businessLayer": "/path/to/spring-project/src/main/java",
      "dataLayer": "/path/to/spring-project/src/main/java"
    }
  }
}
```
</details>

### Microservices Architecture

<details>
<summary>Click to expand Microservices configuration</summary>

```json
{
  "strategies": [
    {
      "name": "API Gateways",
      "type": "REGEX",
      "config": {
        "pattern": ".*Gateway.*"
      },
      "containerMapping": "apiGateway",
      "technology": "Spring Cloud Gateway"
    },
    {
      "name": "Event Handlers",
      "type": "NAME_SUFFIX",
      "config": {
        "suffix": "EventHandler"
      },
      "containerMapping": "eventLayer",
      "technology": "Event-Driven"
    },
    {
      "name": "Message Producers",
      "type": "REGEX",
      "config": {
        "pattern": ".*(?:Publisher|Producer)$"
      },
      "containerMapping": "messagingLayer",
      "technology": "Message Queue"
    }
  ]
}
```
</details>

### Legacy Java Application

<details>
<summary>Click to expand Legacy Java configuration</summary>

```json
{
  "strategies": [
    {
      "name": "DAO Implementations",
      "type": "NAME_SUFFIX",
      "config": {
        "suffix": "DAOImpl"
      },
      "containerMapping": "dataAccess",
      "technology": "JDBC"
    },
    {
      "name": "Business Objects",
      "type": "REGEX",
      "config": {
        "pattern": "com\\.company\\.business\\..*"
      },
      "containerMapping": "businessLogic",
      "technology": "Java"
    },
    {
      "name": "Servlet Controllers",
      "type": "ANNOTATION",
      "config": {
        "annotationType": "javax.servlet.annotation.WebServlet"
      },
      "containerMapping": "webInterface",
      "technology": "Java Servlet"
    }
  ]
}
```
</details>

## 🛠️ Advanced Configuration

### Container Relationships

Define how containers relate to each other:

```java
// In your main generator class
connectorImplementations.uses(connectorApi, "Implements");
connectorImplementations.uses(connectorModel, "Uses");
```

### Component Enrichment

Combine with `componentMapper.json` for detailed component metadata:

```json
{
  "container": [
    {
      "yourContainer": {
        "objectMapper": [
          {
            "componentName": "UserService",
            "tags": "Service",
            "technology": "Spring Service",
            "description": "Manages user operations",
            "relations": [
              {
                "target": "UserRepository",
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

### Global Configuration Options

```json
{
  "globalConfig": {
    "excludeInnerClasses": true,
    "excludeTestClasses": true,
    "basePaths": {
      "container1": "/path1",
      "container2": "/path2"
    },
    "defaultTechnologies": {
      "container1": "Java",
      "container2": "Spring"
    }
  }
}
```

## 🎨 Styling and Visualization

The generator automatically applies styling based on component tags:

```java
// Automatic styling in generated model
styles.addElementStyle("Service").background("#85bb65").color("#ffffff");
styles.addElementStyle("Repository").background("#7560ba").color("#ffffff");
styles.addElementStyle("Controller").background("#e6bd56").color("#000000");
```

## 🔧 Troubleshooting

### Common Issues

1. **Strategy not finding components:**
   - Check annotation types are correct (including imports)
   - Verify base paths point to compiled classes
   - Ensure regex patterns are valid

2. **Missing components:**
   - Check if `enabled: true` is set
   - Verify container mapping names match
   - Check if global filters are excluding components

3. **Compilation errors:**
   - Ensure all required dependencies are in classpath
   - Check JSON syntax is valid
   - Verify annotation classes are available

### Debug Mode

Enable debug output by adding logging:

```java
System.out.println("Applying strategy: " + strategyConfig.getName());
System.out.println("Discovered component: " + component.getName());
```

## 🤝 Contributing

To add new strategy types:

1. Add new enum value to `StrategyType`
2. Implement matcher creation in `StrategyFactory`
3. Add validation logic
4. Update documentation

## 📚 Related Documentation

- [Original Avatar C4 Strategy Documentation](AVATAR_C4_STRATEGY_DOCUMENTATION.md)
- [Configurable Strategy Guide](CONFIGURABLE_STRATEGY_GUIDE.md)
- [Structurizr Documentation](https://structurizr.com/help/documentation)

## 🎉 Benefits

✅ **No Code Changes** - Configure through JSON only  
✅ **Project Agnostic** - Works with any Java project structure  
✅ **Framework Flexible** - Supports Spring, OSGi, Jakarta EE, etc.  
✅ **Convention Aware** - Adapts to your naming conventions  
✅ **Easily Testable** - Validate configurations independently  
✅ **Version Controlled** - Track strategy changes over time  

---

**Ready to generate your C4 model? Configure your strategies and let the magic happen! ✨**
