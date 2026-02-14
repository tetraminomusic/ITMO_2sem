import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {
        ArrayList<Movies> films = new ArrayList<Movies>();
        films.add(new Movies("Star Wars", 1970));
        films.add(new Movies("Iron Man 3", 2013));
        films.add(new Movies("Чебурашка", 2024));
        films.sort(null);
        for (Movies c : films) {
            System.out.println(c.name + " " + c.date_release);
        }
    }
}

class Movies implements Comparable<Movies> {
    String name;
    int date_release;

    public Movies(String name, int date_release) {
        this.name = name;
        this.date_release = date_release;
    }

    public int compareTo(Movies other) {
        return other.date_release - this.date_release;
    }
}