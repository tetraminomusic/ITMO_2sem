package models;

import java.time.LocalDateTime;

/**
 * Класс, описывающий автора лабораторной работы.
 * Содержит информацию о персональных данных: имени, дате рождения, росте, весе и паспорте.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class Person {
    /**
     * Имя автора.
     * Поле не может быть null, строка не может быть пустой.
     */
    private String name;

    /**
     * Дата рождения автора.
     * Поле не может быть null.
     */
    private LocalDateTime birthday;

    /**
     * Рост автора.
     * Значение поля должно быть больше 0.
     */
    private float height;

    /**
     * Вес автора.
     * Поле может быть null, значение поля должно быть больше 0.
     */
    private Double weight;

    /**
     * Идентификатор паспорта автора.
     * Длина строки не должна превышать 50 символов. Поле может быть null.
     */
    private String passportID;

    /**
     * Конструктор для создания объекта Person с проверкой всех входных данных.
     *
     * @param name имя автора (не null, не пустое).
     * @param birthday дата рождения (не null).
     * @param height рост (больше 0).
     * @param weight вес (больше 0 или null).
     * @param passportID номер паспорта (до 50 симв. или null).
     * @throws IllegalArgumentException если параметры не соответствуют установленным ограничениям.
     */
    public Person(String name, LocalDateTime birthday, float height, Double weight, String passportID) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Поле не может быть null, Строка не может быть пустой");
        }
        if (birthday == null) {
            throw new IllegalArgumentException("Поле не может быть null");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Значение поля должно быть больше 0");
        }
        if (weight != null && weight <= 0) {
            throw new IllegalArgumentException("Поле может быть null, Значение поля должно быть больше 0");
        }
        if (passportID != null && passportID.length() > 50) {
            throw new IllegalArgumentException("Длина строки не должна быть больше 50");
        }
        this.name = name;
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
        this.passportID = passportID;
    }
}