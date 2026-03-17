import java.util.concurrent.locks.ReentrantLock;

public class Lock {
    ReentrantLock lock = new ReentrantLock();

    public void method() {
        lock.lock(); //захват мьютекса
        try {
            System.out.println(Thread.currentThread().getName() + "работает сейчас");
        } finally {
            lock.unlock(); //освобождение мьютекса
        }
    }

}

