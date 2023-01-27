package test;

import xmllib.XMLfield;
import xmllib.XMLable;

@XMLable
public class Student {
    @XMLfield(type = "String")
    public String firstName;

    @XMLfield(type = "String", name = "surname")
    public String lastName;

    @XMLfield(type = "int")
    private int age;

    public Student() {
    }

    public Student(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }
}
