package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;
import org.example.utils.JsonSchemaValidator;

import java.io.File;
import java.io.IOException;
import java.util.*;


@Getter
@ToString
public class C4ModelConfigDetail {

    private final WorkspaceDetail workspace;
    private final List<PersonDetail> persons;
    private final List<SoftwareSystemDetail> softwareSystems;

    // Conainer should be created
    private final List<ContainerConfigDetail> containers;
    
    // Keep the original container component mappings
    private final Map<String, ContainerDetail> containerComponentMap = new HashMap<>();

    @JsonCreator
    public C4ModelConfigDetail(
            @JsonProperty("workspace") WorkspaceDetail workspace,
            @JsonProperty("persons") List<PersonDetail> persons,
            @JsonProperty("softwareSystems") List<SoftwareSystemDetail> softwareSystems,
            @JsonProperty("containers") List<ContainerConfigDetail> containers,
            @JsonProperty("container") List<Map<String, ContainerDetail>> containerComponents
    ) {
        this.workspace = workspace;
        this.persons = persons != null ? persons : new ArrayList<>();
        this.softwareSystems = softwareSystems != null ? softwareSystems : new ArrayList<>();
        this.containers = containers != null ? containers : new ArrayList<>();
        
        // Process the container component mappings (existing structure)
        if (containerComponents != null) {
            for (Map<String, ContainerDetail> singleContainerEntry : containerComponents) {
                singleContainerEntry.forEach((name, detail) -> {
                    containerComponentMap.put(name, detail);
                });
            }
        }
    }

    /**
     * Convenience factory method for loading complete C4 model configuration from a JSON file.
     * 
     * @param jsonFile File object pointing to the JSON configuration file
     * @return Fully initialized C4ModelConfig with all elements loaded
     * @throws IOException If the file cannot be read or contains invalid JSON
     */
    public static C4ModelConfigDetail loadFromFile(File jsonFile) throws IOException {
        return loadFromFile(jsonFile, true);
    }
    
    /**
     * Convenience factory method for loading complete C4 model configuration from a JSON file
     * with optional schema validation.
     * 
     * @param jsonFile File object pointing to the JSON configuration file
     * @param validateSchema Whether to validate against the JSON schema
     * @return Fully initialized C4ModelConfig with all elements loaded
     * @throws IOException If the file cannot be read or contains invalid JSON
     * @throws JsonSchemaValidator.ValidationException If schema validation fails
     */
    public static C4ModelConfigDetail loadFromFile(File jsonFile, boolean validateSchema) throws IOException {
        if (validateSchema) {
            validateConfigurationFile(jsonFile);
        }
        
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonFile, C4ModelConfigDetail.class);
    }
    
    /**
     * Validates a C4 model configuration file against the JSON schema.
     * 
     * @param jsonFile The JSON configuration file to validate
     * @throws IOException If the file cannot be read
     * @throws JsonSchemaValidator.ValidationException If validation fails
     */
    public static void validateConfigurationFile(File jsonFile) throws IOException {
        JsonSchemaValidator validator = new JsonSchemaValidator();
        File schemaFile;
        // Try to find schema file in the same directory
        if( !jsonFile.exists() || !jsonFile.isFile()) {
           schemaFile = new File("src/main/java/org/example/json/c4ModelConfigSchema.json");
        }else{
            schemaFile = new File(jsonFile.getParent(), "c4ModelConfigSchema.json");
        }

        
        JsonSchemaValidator.ValidationResult result;
        if (schemaFile.exists()) {
            result = validator.validateJsonFile(jsonFile, schemaFile);
            System.out.println("✓ Using schema file: " + schemaFile.getAbsolutePath());
        } else {
            // Fallback to classpath resource
            try {
                result = validator.validateJsonFileWithResource(jsonFile, "c4ModelConfigSchema.json");
                System.out.println("✓ Using schema from classpath");
            } catch (IOException e) {
                System.out.println("⚠ Warning: Schema validation skipped - schema file not found");
                return;
            }
        }
        
        if (result.isValid()) {
            System.out.println("✓ JSON configuration validation passed");
        } else {
            System.err.println("✗ JSON configuration validation failed:");
            System.err.println(result.getErrorSummary());
            try {
                result.throwIfInvalid();
            } catch (JsonSchemaValidator.ValidationException e) {
                throw new IOException("Configuration validation failed: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Get container component map for backward compatibility.
     * 
     * @return Map of container names to their component details
     */
    public Map<String, ContainerDetail> getContainerMap() {

        return containerComponentMap;
    }
}
