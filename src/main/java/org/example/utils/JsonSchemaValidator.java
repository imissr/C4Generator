package org.example.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * JSON Schema Validator utility class for validating C4 model configuration files.
 * 
 * This class provides validation capabilities to ensure that JSON configuration files
 * conform to the defined schema before processing. It helps catch configuration errors
 * early and provides detailed error messages for troubleshooting.
 * 
 * @author Generated Documentation
 * @version 1.0
 * @since 2025-06-25
 */
public class JsonSchemaValidator {
    
    private final JsonSchemaFactory factory;
    private final ObjectMapper objectMapper;
    
    public JsonSchemaValidator() {
        this.factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Validates a JSON file against a schema file.
     * 
     * @param jsonFile The JSON file to validate
     * @param schemaFile The schema file to validate against
     * @return ValidationResult containing validation status and any errors
     * @throws IOException If files cannot be read
     */
    public ValidationResult validateJsonFile(File jsonFile, File schemaFile) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        JsonNode schemaNode = objectMapper.readTree(schemaFile);
        
        JsonSchema schema = factory.getSchema(schemaNode);
        Set<ValidationMessage> validationMessages = schema.validate(jsonNode);
        
        return new ValidationResult(validationMessages.isEmpty(), validationMessages);
    }
    
    /**
     * Validates a JSON file against a schema from classpath resource.
     * 
     * @param jsonFile The JSON file to validate
     * @param schemaResourcePath The classpath path to the schema file
     * @return ValidationResult containing validation status and any errors
     * @throws IOException If files cannot be read
     */
    public ValidationResult validateJsonFileWithResource(File jsonFile, String schemaResourcePath) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        
        try (InputStream schemaStream = getClass().getClassLoader().getResourceAsStream(schemaResourcePath)) {
            if (schemaStream == null) {
                throw new IOException("Schema resource not found: " + schemaResourcePath);
            }
            
            JsonNode schemaNode = objectMapper.readTree(schemaStream);
            JsonSchema schema = factory.getSchema(schemaNode);
            Set<ValidationMessage> validationMessages = schema.validate(jsonNode);
            
            return new ValidationResult(validationMessages.isEmpty(), validationMessages);
        }
    }
    
    /**
     * Validates a JSON string against a schema file.
     * 
     * @param jsonString The JSON string to validate
     * @param schemaFile The schema file to validate against
     * @return ValidationResult containing validation status and any errors
     * @throws IOException If files cannot be read or JSON cannot be parsed
     */
    public ValidationResult validateJsonString(String jsonString, File schemaFile) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        JsonNode schemaNode = objectMapper.readTree(schemaFile);
        
        JsonSchema schema = factory.getSchema(schemaNode);
        Set<ValidationMessage> validationMessages = schema.validate(jsonNode);
        
        return new ValidationResult(validationMessages.isEmpty(), validationMessages);
    }
    
    /**
     * Validation result containing the validation status and any error messages.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final Set<ValidationMessage> errors;
        
        public ValidationResult(boolean valid, Set<ValidationMessage> errors) {
            this.valid = valid;
            this.errors = errors;
        }
        
        /**
         * @return true if validation passed, false otherwise
         */
        public boolean isValid() {
            return valid;
        }
        
        /**
         * @return Set of validation error messages (empty if validation passed)
         */
        public Set<ValidationMessage> getErrors() {
            return errors;
        }
        
        /**
         * @return Human-readable error summary
         */
        public String getErrorSummary() {
            if (valid) {
                return "Validation passed successfully";
            }
            
            StringBuilder summary = new StringBuilder();
            summary.append("Validation failed with ").append(errors.size()).append(" error(s):\n");
            
            for (ValidationMessage error : errors) {
                summary.append("- ").append(error.getMessage()).append("\n");
            }
            
            return summary.toString();
        }
        
        /**
         * Throws a ValidationException if validation failed.
         * 
         * @throws ValidationException if validation failed
         */
        public void throwIfInvalid() throws ValidationException {
            if (!valid) {
                throw new ValidationException(getErrorSummary());
            }
        }
    }
    
    /**
     * Exception thrown when JSON validation fails.
     */
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
        
        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
