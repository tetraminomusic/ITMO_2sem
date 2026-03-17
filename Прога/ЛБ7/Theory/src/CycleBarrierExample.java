import java.util.concurrent.CyclicBarrier;

public class CycleBarrierExample {
    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("Барьер пройден"));

        Runnable r = () -> {
            try {
                //какой-то код
                barrier.await(); //поток засыпает, ожидая других
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        for (int i = 0; i < 3; i++) {
            new Thread(r).start();
        }
    }
}
