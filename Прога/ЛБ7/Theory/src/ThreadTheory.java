import java.util.concurrent.Callable;

public class ThreadTheory {
    public static void main(String[] args) {
        Thread example1 = new Thread(() -> {
            System.out.println("Новый поток");
        });
        try {
            example1.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Example implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        return 10;
    }
}
