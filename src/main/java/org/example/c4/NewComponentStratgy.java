package org.example.c4;

import com.structurizr.util.StringUtils;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ArrayElementValue;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.ElementValuePair;
import org.osgi.service.component.annotations.Component;
import com.structurizr.component.matcher.TypeMatcher;
import com.structurizr.component.Type;

public class NewComponentStratgy implements TypeMatcher {

    private final String annotationType;
    private final String propertyName;
    private final String property;


    public NewComponentStratgy(String annotationType ,  String propertyName , String property) {
        if (StringUtils.isNullOrEmpty(annotationType)) {
            throw new IllegalArgumentException("An annotation type must be supplied");
        }

        this.annotationType = "L" + annotationType.replace(".", "/") + ";";
        this.propertyName = propertyName;
        this.property = property;
    }

    public NewComponentStratgy(Class<? extends java.lang.annotation.Annotation> annotation,
                               String propertyName, String property, String property1) {
        this.annotationType = "L" + annotation.getCanonicalName().replace('.', '/') + ";";
        this.property = property;

        if (propertyName == null || propertyName.isEmpty()) {
            throw new IllegalArgumentException("A property name must be supplied");
        }
        this.propertyName = propertyName;
    }

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
                // found @Component but not the desired property
                return false;
            }
        }
        // annotation not present
        return false;
    }
}
