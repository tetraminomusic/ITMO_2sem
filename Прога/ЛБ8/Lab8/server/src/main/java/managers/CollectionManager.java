package managers;

import models.Coordinates;
import models.Difficulty;
import models.LabWork;
import models.Person;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Менеджер коллекции, который управляет основной работой коллекции.
 * Может возвращать информацию о коллекции и считывает коллекцию с БД
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class CollectionManager {
    /**
     * Сама коллекция.
     * @new Теперь коллекция синхронизированная, чтобы к ней можно было обратиться только в одной потоке одновременно.
     */
    private final Map<String, LabWork> collection = new ConcurrentHashMap<>();
    /**
     * Менеджер БД для загрузки данных
     */
    private final DatabaseManager databaseManager;
    /**
     * Текущее время.
     */
    private final LocalDateTime lastInitTime = LocalDateTime.now();


    /**
     * Конструктор менеджера
     * @param databaseManager менеджер БД для работы с данными
     */
    public CollectionManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Возвращает хранимую коллекцию.
     * @return коллекция.
     */
    public Map<String, LabWork> getCollection() {
        return collection;
    }

    /**
     * Выводит информацию обо всех элементах коллекции.
     * @return строковое представление информации о коллекции.
     */
    public String info() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String dateString = lastInitTime.format(formatter);
        return "Тип: Map\nДата инициализации: " + dateString + "\nКоличество элементов: " + collection.size();
    }
    /**
     * Проверяет, пуста ли коллекция.
     * @return true, если коллекция пуста
     */
    public boolean isEmpty() {
        return collection.isEmpty();
    }

    /**
     * Загружает все данные из БД в оперативную память.
     */
    public void loadFromDatabase() {
        //очищает текущую коллекцию в память перед загрузкой
        collection.clear();

        String query = "SELECT * FROM labworks";

        try (// создаём объект-запрос к бд
             PreparedStatement ps = databaseManager.getConnection().prepareStatement(query);
            //выполняем запрос и получаем результат
             ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {
                Coordinates coords = new Coordinates(
                        rs.getLong("x"),
                        rs.getLong("y")
                );

                Person author = null;

                if (rs.getString("author_name") != null) {
                    author = new Person(
                            rs.getString("author_name"),
                            rs.getTimestamp("author_birthday").toLocalDateTime(),
                            rs.getFloat("author_height"),
                            rs.getDouble("author_weight"),
                            rs.getString("author_passport_id")
                    );
                }

                //создаём сам объект лабы
                LabWork lab = new LabWork(
                        rs.getInt("id"),
                        rs.getString("name"),
                        coords,
                        rs.getTimestamp("creation_date").toLocalDateTime(),
                        rs.getFloat("minimal_point"),
                        rs.getString("description"),
                        rs.getInt("tuned_in_works"),
                        Difficulty.valueOf(rs.getString("difficulty")),
                        author
                );

                lab.setOwnerLogin(rs.getString("owner_login"));

                collection.put(String.valueOf(lab.getId()), lab);

            }

            System.out.println("Коллекция загружена из БД. Количество элементов: " + collection.size());
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке из БД: " + e.getMessage());
        }
    }
}