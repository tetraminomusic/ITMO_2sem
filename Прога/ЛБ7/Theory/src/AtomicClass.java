import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicClass {
    public static void main(String[] args) {
        AtomicInteger value = new AtomicInteger(10);

        boolean isSuccces = value.compareAndSet(10, 20);
        //если другой поток ничего не менял, то будет true
        //если другой поток успел поменять value, то выдаст false

        AtomicReference<String> string = new AtomicReference<>("Hello");

        string.updateAndGet(old -> old + ", World!");

        System.out.println(string.get());


    }
}


class Counter {
    private final AtomicInteger counter = new AtomicInteger(0);

    public int increment() {
        return counter.getAndIncrement();
    }

    public int get() {
        return counter.get();
    }

}