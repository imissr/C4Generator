package org.example.test;

import org.example.model.C4ModelConfigDetail;
import org.example.utils.JsonSchemaValidator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Comprehensive test class for demonstrating JSON schema validation in C4 model configurations.
 * 
 * <p>This test class showcases the JSON schema validation system that ensures C4 model
 * configurations conform to the expected structure and contain all required elements.
 * It provides practical examples of both valid and invalid configurations to demonstrate
 * how the validation system catches common configuration errors.</p>
 * 
 * <p>The validation system is crucial for maintaining data quality and preventing
 * runtime errors during C4 model generation. By validating configurations upfront,
 * the system can provide clear error messages and guidance for fixing issues.</p>
 * 
 * <p><strong>Test scenarios covered:</strong></p>
 * <ul>
 *   <li><strong>Valid configuration validation</strong> - Confirms well-formed JSON passes validation</li>
 *   <li><strong>Invalid configuration detection</strong> - Shows how missing required fields are caught</li>
 *   <li><strong>Schema violation examples</strong> - Demonstrates validation error reporting</li>
 *   <li><strong>Programmatic validation</strong> - Shows how to use the validation API</li>
 * </ul>
 * 
 * <p><strong>Key benefits demonstrated:</strong></p>
 * <ul>
 *   <li>Early error detection before model generation</li>
 *   <li>Clear, actionable error messages for debugging</li>
 *   <li>Consistent configuration structure enforcement</li>
 *   <li>Integration with existing Jackson-based JSON processing</li>
 * </ul>
 * 
 * <p>The test creates temporary JSON files with different configuration scenarios
 * and validates them against the C4 model schema, showing both successful
 * validation and detailed error reporting for invalid configurations.</p>
 * 
 * <p>Usage:</p>
 * <pre>
 * java org.example.test.JsonSchemaValidationTest
 * </pre>
 * 
 * @see JsonSchemaValidator for the validation implementation
 * @see C4ModelConfigDetail for the configuration structure being validated
 * 
 * @author C4 Model Generator
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
            C4ModelConfigDetail config = C4ModelConfigDetail.loadFromFile(validFile, true);
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
            C4ModelConfigDetail config = C4ModelConfigDetail.loadFromFile(invalidFile, true);
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
            C4ModelConfigDetail config = C4ModelConfigDetail.loadFromFile(invalidFile, true);
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
