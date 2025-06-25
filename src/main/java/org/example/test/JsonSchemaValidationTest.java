package org.example.test;

import org.example.componentDetail.C4ModelConfig;
import org.example.componentDetail.JsonSchemaValidator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Test class to demonstrate JSON schema validation for C4 model configurations.
 * 
 * This class provides examples of both valid and invalid configurations to show
 * how the schema validation works in practice.
 * 
 * @author Generated Documentation
 * @version 1.0
 * @since 2025-06-25
 */
public class JsonSchemaValidationTest {
    
    public static void main(String[] args) {
        System.out.println("=== JSON Schema Validation Test ===\n");
        
        try {
            // Test 1: Valid configuration
            testValidConfiguration();
            
            // Test 2: Invalid configuration (missing required fields)
            testInvalidConfiguration();
            
            // Test 3: Invalid configuration (wrong data types)
            testInvalidDataTypes();
            
        } catch (Exception e) {
            System.err.println("Test execution failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testValidConfiguration() throws IOException {
        System.out.println("1. Testing VALID configuration...");
        
        String validJson = """
        {
          "workspace": {
            "name": "Test Workspace",
            "description": "A test workspace for validation"
          },
          "persons": [
            {
              "name": "Test User",
              "description": "A test user",
              "relations": [
                {
                  "target": "Test System",
                  "type": "uses"
                }
              ]
            }
          ],
          "softwareSystems": [
            {
              "name": "Test System",
              "description": "A test software system",
              "relations": []
            }
          ],
          "containers": [
            {
              "name": "Test Container",
              "description": "A test container",
              "technology": "Java/Spring Boot",
              "relations": []
            }
          ],
          "container": [
            {
              "testContainer": {
                "objectMapper": [
                  {
                    "componentName": "Test Component",
                    "tags": "Service",
                    "technology": "Java",
                    "description": "A test component"
                  }
                ]
              }
            }
          ]
        }
        """;
        
        File validFile = new File("test-valid-config.json");
        try (FileWriter writer = new FileWriter(validFile)) {
            writer.write(validJson);
        }
        
        try {
            C4ModelConfig config = C4ModelConfig.loadFromFile(validFile, true);
            System.out.println("✓ Valid configuration loaded successfully");
            System.out.println("  - Workspace: " + config.getWorkspace().getName());
            System.out.println("  - Persons: " + config.getPersons().size());
            System.out.println("  - Systems: " + config.getSoftwareSystems().size());
            System.out.println("  - Containers: " + config.getContainers().size());
        } finally {
            validFile.delete();
        }
        
        System.out.println();
    }
    
    private static void testInvalidConfiguration() throws IOException {
        System.out.println("2. Testing INVALID configuration (missing required fields)...");
        
        String invalidJson = """
        {
          "workspace": {
            "name": "Test Workspace"
          },
          "persons": [
            {
              "name": "Test User"
            }
          ],
          "softwareSystems": [],
          "containers": [
            {
              "name": "Test Container",
              "description": "A test container"
            }
          ]
        }
        """;
        
        File invalidFile = new File("test-invalid-config.json");
        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write(invalidJson);
        }
        
        try {
            C4ModelConfig config = C4ModelConfig.loadFromFile(invalidFile, true);
            System.out.println("✗ Should have failed validation!");
        } catch (IOException e) {
            System.out.println("✓ Correctly caught validation error:");
            System.out.println("  " + e.getMessage());
        } finally {
            invalidFile.delete();
        }
        
        System.out.println();
    }
    
    private static void testInvalidDataTypes() throws IOException {
        System.out.println("3. Testing INVALID configuration (wrong data types)...");
        
        String invalidJson = """
        {
          "workspace": {
            "name": 123,
            "description": "A test workspace"
          },
          "persons": "not an array",
          "softwareSystems": [
            {
              "name": "Test System",
              "description": "A test software system",
              "relations": []
            }
          ],
          "containers": [
            {
              "name": "Test Container",
              "description": "A test container",
              "technology": "Java/Spring Boot",
              "relations": []
            }
          ]
        }
        """;
        
        File invalidFile = new File("test-invalid-types.json");
        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write(invalidJson);
        }
        
        try {
            C4ModelConfig config = C4ModelConfig.loadFromFile(invalidFile, true);
            System.out.println("✗ Should have failed validation!");
        } catch (IOException e) {
            System.out.println("✓ Correctly caught validation error:");
            System.out.println("  " + e.getMessage());
        } finally {
            invalidFile.delete();
        }
        
        System.out.println();
    }
}
