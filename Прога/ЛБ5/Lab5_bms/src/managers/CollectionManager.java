package managers;

import models.LabWork;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CollectionManager {
    private final LinkedHashMap<String, LabWork> collection = new LinkedHashMap<>();
    private final LocalDateTime lastInitTime = LocalDateTime.now();


    public LinkedHashMap<String, LabWork> getCollection() {
        return collection;
    }

    public Integer generateNextId() { //создание уникального ID для лабы
        if (collection.isEmpty()) {return 1;}

        Integer maxId = 0;
        for (LabWork laba: collection.values()) {
            if (laba.getId() > maxId) {maxId = laba.getId();}
        }
        return maxId+1;
    }

    public void clear() {
        collection.clear();
    }

    public String info() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String dateString = lastInitTime.format(formatter);
        return "Тип: LinkedHashMap\nДата инициализации: " + dateString + "\nКоличество элементов: "  + collection.size();
    }

}
