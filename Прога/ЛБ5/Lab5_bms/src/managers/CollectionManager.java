package managers;

import models.LabWork;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Менеджер коллекции, который управляет основной работой коллекции.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class CollectionManager {
    /**
     * Сама коллекция.
     */
    private final LinkedHashMap<String, LabWork> collection = new LinkedHashMap<>();
    /**
     * Текущее время.
     */
    private final LocalDateTime lastInitTime = LocalDateTime.now();


    /**
     * Возвращает хранимую коллекцию.
     * @return коллекция.
     */
    public LinkedHashMap<String, LabWork> getCollection() {
        return collection;
    }

    /**
     * Создаёт уникальный ID для элемента коллекции.
     * @return уникальный ID.
     */
    public Integer generateNextId() { //создание уникального ID для лабы
        if (collection.isEmpty()) {return 1;}

        Integer maxId = 0;
        for (LabWork laba: collection.values()) {
            if (laba.getId() > maxId) {maxId = laba.getId();}
        }
        return maxId+1;
    }

    /**
     * Удаляет элементы все коллекции.
     */
    public void clear() {
        collection.clear();
    }

    /**
     * Выводит информацию обо всех элементах коллекции.
     * @return строковое представление информации о коллекции.
     */
    public String info() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String dateString = lastInitTime.format(formatter);
        return "Тип: LinkedHashMap\nДата инициализации: " + dateString + "\nКоличество элементов: "  + collection.size();
    }

}
