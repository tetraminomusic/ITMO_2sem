public class Semaphore {

    public static void main(String[] args) {
        java.util.concurrent.Semaphore semaphore = new java.util.concurrent.Semaphore(2); //два потока одновременно

        Runnable task = () -> {
            try {
                semaphore.acquire(); //выдаём разрешение
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release(); //отдаём разрешение
            }
        };

        for (int i = 0; i < 5; i++) new Thread(task).start();

    }
}
