package test;

import java.io.IOException;
import xmllib.XMLSerializer;

public class Main {
    public static void main(String[] args) {
        // Students
        Student student1 = new Student("Antonio", "Strippoli", 24);
        Student student2 = new Student();

        // Non-serializable object
        String string1 = "";

        // Teachers
        Teacher teacher1 = new Teacher(0, "Nome Cognome", 30);
        Teacher teacher2 = new Teacher();

        // Serialize all the objects
        Object[] arr = {student1, student2, string1, teacher1, teacher2};
        try {
            XMLSerializer.serialize(arr, "output");
        }catch (IOException | IllegalAccessException e){
            System.out.println("Failed to serialize objects: " + e);
        }
    }
}
