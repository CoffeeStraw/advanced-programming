package test;

import xmllib.XMLfield;
import xmllib.XMLable;

@XMLable
public class Teacher {
    private final int id;

    @XMLfield(type = "String", name = "name")
    public final String name;

    @XMLfield(type = "int")
    private final int age;

    public Teacher() {
        this.id = -1;
        this.name = null;
        this.age = -1;
    }

    public Teacher(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
