import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConcurentHashMap {

    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.putIfAbsent("Apple", 10); //добавляет элемент, если такого нет в мэп
        map.compute("Apple", (key, value) -> value + 1);

        CopyOnWriteArrayList<Integer> array = new CopyOnWriteArrayList<>();
        array.add(10); //дорогостоящая операция из-за копирования
        System.out.println(array.get(0)); //чтение происходит мгновенно и без блокировок

    }

}
