package org.example.strategies;

import com.structurizr.util.StringUtils;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ArrayElementValue;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.ElementValuePair;
import org.osgi.service.component.annotations.Component;
import com.structurizr.component.matcher.TypeMatcher;
import com.structurizr.component.Type;

/**
 * Custom component discovery strategy for OSGi components with specific property annotations.
 * 
 * <p>This TypeMatcher implementation extends Structurizr's component discovery capabilities
 * by providing advanced annotation-based matching for OSGi components. It's specifically
 * designed to handle complex annotation property structures where components are identified
 * not just by the presence of an annotation, but by specific property values within that annotation.</p>
 * 
 * <p>The strategy is particularly valuable for OSGi-based architectures where components
 * use property arrays in their {@code @Component} annotations to declare capabilities,
 * service categories, or connector types. Traditional annotation matchers only check for
 * annotation presence, but this implementation can examine the actual property values.</p>
 * 
 * <p><strong>Key capabilities:</strong></p>
 * <ul>
 *   <li><strong>Property-aware matching</strong> - Examines annotation property arrays, not just annotation presence</li>
 *   <li><strong>Bytecode analysis</strong> - Uses Apache BCEL for deep annotation inspection</li>
 *   <li><strong>Flexible property matching</strong> - Supports prefix-based property value matching</li>
 *   <li><strong>OSGi integration</strong> - Designed for OSGi component property conventions</li>
 * </ul>
 * 
 * <p><strong>Usage examples:</strong></p>
 * <ul>
 *   <li>Match components with {@code @Component(property="connector=isma.himsa")}</li>
 *   <li>Filter components by service categories: {@code property="category=security"}</li>
 *   <li>Identify components with specific capabilities: {@code property="provides=encryption"}</li>
 * </ul>
 * 
 * <p><strong>Implementation details:</strong></p>
 * <p>The matcher uses BCEL (Byte Code Engineering Library) to inspect class bytecode
 * and examine annotation metadata. It searches for the target annotation type, then
 * examines the specified property attribute (typically "property" in OSGi @Component
 * annotations) looking for array elements that start with the desired property name.</p>
 * 
 * <p><strong>Example annotation that would match:</strong></p>
 * <pre>     * @see #NewComponentStratgy(String, String, String) for string-based construction

 * {@literal @}Component(
 *     property = {
 *         "connector=isma.himsa",
 *         "service.description=Healthcare Data Connector",
 *         "version=2.1"
 *     }
 * )
 * public class HealthcareConnector { ... }
 * </pre>
 * 
 * <p>With configuration: annotationType="org.osgi.service.component.annotations.Component",
 * propertyName="connector", annotationProperty="property", this would match the above class
 * because the property array contains "connector=isma.himsa".</p>
 * 
 * <p><strong>Note:</strong> This class was previously named "NewComponentStratgy" (with a typo).
 * The name has been corrected to "NewComponentStrategy" to maintain consistency with naming conventions.</p>
 * 
 * @see TypeMatcher for the Structurizr component matching interface
 * @see Component for the OSGi component annotation this is typically used with
 * 
 * @author C4 Model Generator
 * @version 1.0
 * @since 2025-06-15
 */
public class NewComponentStrategy implements TypeMatcher {
    /** 
     * The target annotation type in internal JVM format.
     * 
     * <p>Stored in JVM internal format (e.g., "Lorg/osgi/service/component/annotations/Component;")
     * for efficient bytecode comparison during annotation inspection.</p>
     */
    private final String annotationType;
    
    /** 
     * The property name to search for within the annotation property array.
     * 
     * <p>This is the prefix used to identify relevant property entries. For example,
     * if set to "connector", it will match array elements like "connector=isma.himsa".</p>
     */
    private final String propertyName;
    
    /** 
     * The annotation attribute name that contains the property array.
     * 
     * <p>Typically "property" for OSGi @Component annotations, but can be any
     * annotation attribute that contains a String array of property definitions.</p>
     */
    private final String property;

    /**
     * Creates a new component strategy matcher using string-based annotation type specification.
     * 
     * <p>This constructor accepts a fully qualified annotation class name and converts it
     * to the internal JVM format required for bytecode analysis. It's the preferred
     * constructor when working with configuration systems that use string-based
     * class references.</p>
     * 
     * <p>The annotation type will be converted from dot notation (e.g., 
     * "org.osgi.service.component.annotations.Component") to JVM internal format
     * (e.g., "Lorg/osgi/service/component/annotations/Component;") automatically.</p>
     * 
     * @param annotationType The fully qualified annotation class name in standard Java notation
     *                      (e.g., "org.osgi.service.component.annotations.Component").
     *                      Must not be null or empty.
     * @param propertyName The name of the property to match within the annotation property array
     *                    (e.g., "connector"). This will be used as a prefix to match property entries.
     * @param property The annotation attribute name that contains the property array
     *                (e.g., "property" for OSGi @Component annotations).
     * @throws IllegalArgumentException if annotationType is null or empty
     *
     */
    public NewComponentStrategy(String annotationType ,  String propertyName , String property) {
        if (StringUtils.isNullOrEmpty(annotationType)) {
            throw new IllegalArgumentException("An annotation type must be supplied");
        }

        this.annotationType = "L" + annotationType.replace(".", "/") + ";";
        this.propertyName = propertyName;
        this.property = property;
    }    /**
     * Creates a new component strategy matcher using a Class reference for the annotation.
     * 
     * <p>This constructor provides type safety by accepting a Class object for the
     * annotation type rather than a string. It automatically extracts the canonical
     * name and converts it to the required JVM internal format.</p>
     * 
     * <p>This approach is preferred when the annotation class is available at compile
     * time and you want to benefit from IDE autocompletion and refactoring support.</p>
     * 
     * @param annotation The annotation class to match (e.g., Component.class).
     *                  Must be a valid annotation type.
     * @param propertyName The name of the property to match within the annotation property array.
     *                    Must not be null or empty.
     * @param property The annotation attribute name that contains the property array
     *                (typically "property" for OSGi components).
     * @param property1 Additional property parameter (currently unused - reserved for future extensions).
     * @throws IllegalArgumentException if propertyName is null or empty
     * 
     */
    public NewComponentStrategy(Class<? extends java.lang.annotation.Annotation> annotation,
                               String propertyName, String property, String property1) {
        this.annotationType = "L" + annotation.getCanonicalName().replace('.', '/') + ";";
        this.property = property;

        if (propertyName == null || propertyName.isEmpty()) {
            throw new IllegalArgumentException("A property name must be supplied");
        }
        this.propertyName = propertyName;
    }    /**
     * Determines if the given type matches the component discovery criteria.
     * 
     * This method examines the bytecode of the provided type to check if it has
     * the target annotation with the specified property value. It uses BCEL to
     * parse annotation metadata and search for array-type properties that contain
     * the desired property name-value pair.
     * 
     * The matching process:
     * 1. Iterates through all annotations on the class
     * 2. Finds the target annotation type
     * 3. Examines the specified property (which should be an array)
     * 4. Searches array elements for the propertyName prefix
     * 
     * @param type The type to examine for matching annotations
     * @return true if the type has the target annotation with the specified property, false otherwise
     * @throws IllegalArgumentException if type is null or lacks bytecode information
     */
    @Override
    public boolean matches(Type type) {
        if (type == null || type.getJavaClass() == null) {
            throw new IllegalArgumentException("A non-null type with bytecode is required");
        }
        for (AnnotationEntry entry : type.getJavaClass().getAnnotationEntries()) {
            if (annotationType.equals(entry.getAnnotationType())) {
                // look for the "property" element in @Component(...)
                for (ElementValuePair pair : entry.getElementValuePairs()) {
                    if (this.property.equals(pair.getNameString())
                            && pair.getValue() instanceof ArrayElementValue) {

                        ArrayElementValue array = (ArrayElementValue)pair.getValue();
                        for (ElementValue ev : array.getElementValuesArray()) {
                            // each ev is a string like "connector=isma.himsa"
                            String prop = ev.stringifyValue();
                            if (prop.startsWith(propertyName + "=")) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }
}
