import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Notify {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition lightIsOn = lock.newCondition(); //условие
    private boolean isLight = false;

    public void waitForLight() throws InterruptedException {
        lock.lock();
        try {
            while (!isLight) {
                lightIsOn.await(); //уснул и попадает в конкретную комнату сна
            }
            //не попал в комнату
        } finally {
            lock.unlock();
        }
    }

    public void switchOn() {
        lock.lock();
        try {
            isLight = true;
            lightIsOn.notify();
        } finally {
            lock.unlock();
        }
    }
}