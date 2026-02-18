package Io;

import java.io.*;

public class FilesInJava {
    public static  void main(String[] args)  {
        try (FileWriter file = new FileWriter("file.txt", true)) {
            file.write("Абоба");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
