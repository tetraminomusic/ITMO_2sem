
import java.io.*;
import java.io.IOException;

public class FileTheory {
    public static void main(String[] args) {

        try ( ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Sample"))) {
            out.writeObject((Integer) 200);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static class Person {
        int age;
        String name;
        public Person(int age, String name) {
            this.age = age;
            this.name = name;
        }
    }
}
