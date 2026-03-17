import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolExample {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 4; i++) {
            pool.execute(() -> {
                System.out.println("Выполняется таск");
            });
        }

        pool.shutdown(); //обязательно закрывает пул, иначе программа будет работать до бесконечности

    }
}
