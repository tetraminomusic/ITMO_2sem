import java.util.Arrays;
import java.util.List;

public class FlatMapExample {
    public static void main(String[] args) {
        List<String> sentences = Arrays.asList("Всем привет", "Меня зовут", "Артур Пирожков");
        sentences.stream()
                .flatMap(s -> Arrays.stream(s.split("\\s+")))
                .forEach(s -> System.out.print(s + " "));
    }
}
