import java.util.concurrent.CountDownLatch;

public class CounDownLatch {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3); //нужно 3 подтверждения
        //3 рабочих потока
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
               System.out.println("Этап завершён");
               latch.countDown();
            }).start();
        }
        latch.await(); //главный поток спит, пока счётчик не уменьшиться на 1
        System.out.println("Все этапы завершены");

    }

}
