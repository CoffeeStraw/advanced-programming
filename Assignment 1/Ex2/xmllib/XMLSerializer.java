package xmllib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class XMLSerializer {

    /**
     * Represents the introspection of a field.
     */
    private static class FieldStructure {
        private String name;
        private String type;
        private Field field;

        public FieldStructure(String name, String type, Field field) {
            this.name = name;
            this.type = type;
            this.field = field;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public Field getField() {
            return field;
        }
    }

    /**
     * Represents the introspection of a class.
     */
    private static class ClassStructure {
        private String name;
        private ArrayList<FieldStructure> fields;

        public ClassStructure(String name, ArrayList<FieldStructure> fields) {
            this.name = name;
            this.fields = fields;
        }

        public String getName() {
            return name;
        }

        public ArrayList<FieldStructure> getFields() {
            return fields;
        }
    }

    public static void serialize(Object[] arr, String fileName) throws IOException, IllegalAccessException {
        // Start serialization
        StringBuilder xmlText = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Objects>\n");

        // Serialize objects
        HashMap<String, ClassStructure> introspectedClasses = new HashMap<>();
        for (Object object : arr) {
            Class<?> klass = object.getClass();
            String name = klass.getSimpleName();

            // Classes without XMLable annotation are not parsable
            if (!klass.isAnnotationPresent(XMLable.class)) {
                xmlText.append("\t<notXMLable />\n");
                continue;
            }

            // Get class' introspection and compute if not present
            ClassStructure introspectedClass = introspectedClasses.computeIfAbsent(name, k -> introspectClass(klass));

            // Serialize and save for future re-usage
            String objectXML = serializeObject(object, introspectedClass);
            xmlText.append(objectXML);
        }

        xmlText.append("</Objects>\n");

        // Create output file (if not present)
        File outputFile = new File(fileName + ".xml");
        outputFile.createNewFile();

        // Write to output file
        FileWriter outputFileWriter = new FileWriter(outputFile);
        outputFileWriter.write(xmlText.toString());
        outputFileWriter.close();
    }

    private static ClassStructure introspectClass(Class<?> objectClass) {
        // Instrospect class name
        String objectClassName = objectClass.getSimpleName();
        
        // Introspect fields
        ArrayList<FieldStructure> fields = new ArrayList<>();
        for (Field field : objectClass.getDeclaredFields()) {
            // Introspect field type and annotation
            var fieldType = field.getType();
            XMLfield annotation = field.getAnnotation(XMLfield.class);

            // Skip if not primitive or String, or if XMLField annotation is not present
            if ((!fieldType.isPrimitive() && !fieldType.equals(String.class)) || annotation == null) {
                continue;
            }

            // Get access to private fields (and later restore it)
            field.setAccessible(true);

            // Get information from annotation
            String fieldName = annotation.name().equals("") ? field.getName() : annotation.name();
            String annotationType = annotation.type();

            // Save field
            fields.add(new FieldStructure(fieldName, annotationType, field));
        }

        return new ClassStructure(objectClassName, fields);
    }

    private static String serializeObject(Object object, ClassStructure introspectedClass)
            throws IllegalAccessException {
        // Serialize class name
        String objectClassName = introspectedClass.getName();
        StringBuilder xmlText = new StringBuilder("\t<" + objectClassName + ">\n");

        // Serialize fields
        for (FieldStructure toUnpack : introspectedClass.getFields()) {
            // Unpack
            String fieldName = toUnpack.getName();
            String fieldType = toUnpack.getType();
            Field field = toUnpack.getField();

            // Serialize field
            // NOTE: The field could contain "<" or ">" character,
            // potentially corrupting the XML.
            // Unfortunately, there is no easily accessible function
            // to escape XML strings in Java 11.
            // We do not overcomplicate the solution and we limit ourselves to simply
            // acknowledging the problem.
            xmlText.append("\t\t<" + fieldName + " type=\"" + fieldType + "\">");
            xmlText.append(field.get(object));
            xmlText.append("</" + fieldName + ">\n");
        }

        // Close serialization
        xmlText.append("\t</" + objectClassName + ">\n");
        return xmlText.toString();
    }
}