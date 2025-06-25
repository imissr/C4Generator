package org.example.c4;

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
 * This TypeMatcher implementation extends Structurizr's component discovery capabilities
 * by matching OSGi components based on specific annotation properties. It's particularly
 * useful for identifying components that have custom properties in their @Component
 * annotations, such as connector types or service categories.
 * 
 * The strategy uses Apache BCEL (Byte Code Engineering Library) to inspect bytecode
 * and examine annotation metadata at the class level.
 * 
 * Example usage:
 * - Match components with @Component(property="connector=isma.himsa")
 * - Filter components by specific service properties
 * - Identify components with particular configuration attributes
 * 
 * @author Generated Documentation
 * @version 1.0
 * @since 2025-06-15
 */
public class NewComponentStratgy implements TypeMatcher {
    /** The target annotation type in internal JVM format (e.g., "Lorg/osgi/service/component/annotations/Component;") */
    private final String annotationType;
    
    /** The property name to search for within the annotation (e.g., "connector") */
    private final String propertyName;
    
    /** The annotation property to examine (e.g., "property") */
    private final String property;

    /**
     * Creates a new component strategy matcher using string-based annotation type.
     * 
     * @param annotationType The fully qualified annotation class name (e.g., "org.osgi.service.component.annotations.Component")
     * @param propertyName The name of the property to match within the annotation (e.g., "connector")  
     * @param property The annotation attribute to examine (e.g., "property")
     * @throws IllegalArgumentException if annotationType is null or empty
     */
    public NewComponentStratgy(String annotationType ,  String propertyName , String property) {
        if (StringUtils.isNullOrEmpty(annotationType)) {
            throw new IllegalArgumentException("An annotation type must be supplied");
        }

        this.annotationType = "L" + annotationType.replace(".", "/") + ";";
        this.propertyName = propertyName;
        this.property = property;
    }    /**
     * Creates a new component strategy matcher using a Class reference for the annotation.
     * 
     * @param annotation The annotation class to match (e.g., Component.class)
     * @param propertyName The name of the property to match within the annotation
     * @param property The annotation attribute to examine  
     * @param property1 Additional property parameter (currently unused)
     * @throws IllegalArgumentException if propertyName is null or empty
     */
    public NewComponentStratgy(Class<? extends java.lang.annotation.Annotation> annotation,
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
