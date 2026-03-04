import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StreamAPI {
    public static void main(String[] args) {
        ArrayList<String> nameList = new ArrayList<>();
        insert_names(nameList);
        List<String> longNames = nameList.stream()
                .map(name -> name.toUpperCase()) //промежуточная операция
                .toList(); //терминальная операция
        for (String name: longNames) {
            System.out.println(name);
        }
    }
    private static void insert_names(ArrayList<String> list) {
        list.add("Кирилл");
        list.add("Дима");
        list.add("Серёга");
        list.add("Ян");
        list.add("Алексей");
        list.add("Александр");
    }
}
