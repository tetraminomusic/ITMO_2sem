import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorExample {
    public static void main(String[] args) throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(2); //пул из 2-х потоков

        Callable<Integer> task = () -> {
            Thread.sleep(1000);
            return 42;
        };

        Future<Integer> future = service.submit(task);

        //что-то происходит

        Integer result = future.get(); //блокирующий метод

        System.out.println(result);

        service.shutdown(); //обязательно закрываем пул

    }

}
