import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Generics {
    public static void main(String[] args) {

    }

    static class Person {
        private String id;

        public <T>Person(T id) {
            this.id = id.toString();
        }
    }

}

