package xmllib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * This additional class is an alternative to XMLSerializer.
 * It does always introspect the class structure,
 * even if it has already been seen before.
 * 
 * It doesn't follow the request, but it is more compact and space-efficient.
 */
public class XMLSerializerAlwaysIntrospect {

    public static void serialize(Object[] arr, String fileName) throws IOException, IllegalAccessException {
        StringBuilder xmlText = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Objects>\n");

        // Serialize objects
        for (Object object : arr) {
            Class<?> klass = object.getClass();

            // Classes without XMLable annotation are not parsable
            if (!klass.isAnnotationPresent(XMLable.class)) {
                xmlText.append("\t<notXMLable />\n");
                continue;
            }

            // Serialize and save for future re-usage
            String objectXML = serializeObject(object);
            xmlText.append(objectXML);
        }

        xmlText.append("</Objects>\n");

        // Create output file
        File outputFile = new File(fileName + ".xml");
        outputFile.createNewFile();

        // Write to output file
        FileWriter outputFileWriter = new FileWriter(outputFile);
        outputFileWriter.write(xmlText.toString());
        outputFileWriter.close();
    }

    private static String serializeObject(Object object) throws IllegalAccessException {
        // Start serialization
        Class<?> klass = object.getClass();
        String objectClassName = klass.getSimpleName();
        StringBuilder xmlText = new StringBuilder("\t<" + objectClassName + ">\n");

        // Serialize fields
        for (Field field : klass.getDeclaredFields()) {
            var fieldType = field.getType();
            if (!fieldType.isPrimitive() && !fieldType.equals(String.class)) {
                continue;
            }

            // Get access to private fields
            field.setAccessible(true);

            // Skip if XMLfield annotation is not present
            XMLfield annotation = field.getAnnotation(XMLfield.class);
            if (annotation == null) {
                continue;
            }

            // Serialize field
            String fieldName = annotation.name().equals("") ? field.getName() : annotation.name();
            xmlText.append("\t\t<" + fieldName + " type=\"" + annotation.type() + "\">");
            xmlText.append(field.get(object));
            xmlText.append("</" + fieldName + ">\n");
        }

        // Close serialization
        xmlText.append("\t</" + objectClassName + ">\n");
        return xmlText.toString();
    }
}