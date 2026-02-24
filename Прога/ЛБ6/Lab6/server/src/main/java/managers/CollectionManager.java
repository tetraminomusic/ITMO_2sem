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
    public Integer generateNextId() {
        if (collection.isEmpty()) {
            return 1;
        }

        Integer maxId = 0;
        for (LabWork laba: collection.values()) {
            if (laba.getId() > maxId) {
                maxId = laba.getId();
            }
        }
        return maxId + 1;
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
        return "Тип: LinkedHashMap\nДата инициализации: " + dateString + "\nКоличество элементов: " + collection.size();
    }
    /**
     * CREATE - Добавляет новый элемент в коллекцию.
     * @param key ключ элемента
     * @param labWork элемент для добавления
     * @return true, если элемент успешно добавлен
     */
    public boolean add(String key, LabWork labWork) {
        if (key == null || key.trim().isEmpty() || labWork == null) {
            return false;
        }
        if (containsId(labWork.getId())) {
            return false;
        }

        collection.put(key, labWork);
        return true;
    }

    /**
     * READ - Получает элемент по ключу.
     * @param key ключ элемента
     * @return элемент или null, если не найден
     */
    public LabWork getByKey(String key) {
        return collection.get(key);
    }

    /**
     * READ - Получает элемент по ID.
     * @param id идентификатор элемента
     * @return элемент или null, если не найден
     */
    public LabWork getById(Integer id) {
        if (id == null) {
            return null;
        }

        for (LabWork labWork : collection.values()) {
            if (labWork.getId().equals(id)) {
                return labWork;
            }
        }
        return null;
    }

    /**
     * READ - Возвращает все элементы коллекции.
     * @return коллекция значений
     */
    public Collection<LabWork> getAll() {
        return collection.values();
    }

    /**
     * UPDATE - Обновляет существующий элемент по ключу.
     * @param key ключ элемента
     * @param newLabWork новый элемент
     * @return true, если элемент успешно обновлён
     */
    public boolean update(String key, LabWork newLabWork) {
        if (!collection.containsKey(key) || newLabWork == null) {
            return false;
        }

        // Проверяем, что новый ID уникален (если он изменился)
        Integer oldId = collection.get(key).getId();
        Integer newId = newLabWork.getId();

        if (!oldId.equals(newId) && containsId(newId)) {
            return false;
        }

        collection.put(key, newLabWork);
        return true;
    }

    /**
     * DELETE - Удаляет элемент по ключу.
     * @param key ключ элемента
     * @return удалённый элемент или null, если элемент не найден
     */
    public LabWork remove(String key) {
        return collection.remove(key);
    }

    /**
     * DELETE - Удаляет элемент по ID.
     * @param id идентификатор элемента
     * @return true, если элемент успешно удалён
     */
    public boolean removeById(Integer id) {
        if (id == null) {
            return false;
        }

        String keyToRemove = findKeyById(id);
        if (keyToRemove != null) {
            collection.remove(keyToRemove);
            return true;
        }
        return false;
    }

    /**
     * Проверяет существование элемента по ключу.
     * @param key ключ элемента
     * @return true, если элемент существует
     */
    public boolean containsKey(String key) {
        return collection.containsKey(key);
    }

    /**
     * Проверяет существование элемента по ID.
     * @param id идентификатор элемента
     * @return true, если элемент существует
     */
    public boolean containsId(Integer id) {
        return getById(id) != null;
    }

    /**
     * Возвращает размер коллекции.
     * @return количество элементов
     */
    public int size() {
        return collection.size();
    }

    /**
     * Проверяет, пуста ли коллекция.
     * @return true, если коллекция пуста
     */
    public boolean isEmpty() {
        return collection.isEmpty();
    }

    /**
     * Находит ключ по ID элемента.
     * @param id идентификатор элемента
     * @return ключ элемента или null, если не найден
     */
    private String findKeyById(Integer id) {
        if (id == null) {
            return null;
        }

        for (Map.Entry<String, LabWork> entry : collection.entrySet()) {
            if (entry.getValue().getId().equals(id)) {
                return entry.getKey();
            }
        }
        return null;
    }
}