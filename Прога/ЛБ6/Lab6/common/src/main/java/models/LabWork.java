package models;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Класс лабораторной работы.
 * Реализует интерфейс {@link Comparable} для обеспечения сортировки по умолчанию по имени.
 * Содержит информацию о названии, координатах, баллах и авторе работы.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class LabWork implements Comparable<LabWork>, Serializable {

    private static final long serialVersionUID = 1234L;

    /**
     * Уникальный идентификатор работы.
     * Поле не может быть null, значение должно быть больше 0, уникально и генерируется автоматически.
     */
    private Integer id;

    /**
     * Название лабораторной работы.
     * Поле не может быть null, строка не может быть пустой.
     */
    private String name;

    /**
     * Координаты работы.
     * Поле не может быть null.
     */
    private Coordinates coordinates;

    /**
     * Дата и время создания объекта.
     * Поле не может быть null, значение генерируется автоматически.
     */
    private LocalDateTime creationDate;

    /**
     * Минимальный балл за работу.
     * Значение поля должно быть больше 0.
     */
    private float minimalPoint;

    /**
     * Описание работы.
     * Поле может быть null.
     */
    private String description;

    /**
     * Количество настроенных работ.
     * Поле может быть null.
     */
    private Integer tunedInWorks;

    /**
     * Сложность работы.
     * Поле не может быть null.
     */
    private Difficulty difficulty;

    /**
     * Автор работы.
     * Поле может быть null.
     */
    private Person author;

    /**
     * Сравнивает текущий объект с другим объектом типа LabWork.
     * Сравнение происходит по алфавитному порядку названий работ.
     *
     * @param other объект для сравнения.
     * @return отрицательное число, ноль или положительное число в зависимости от результата сравнения.
     */
    //Логика сравнения двух лабораторных - алфавитный порядок по имени
    @Override
    public int compareTo(LabWork other) {
        return this.getCoordinates().compareTo(other.getCoordinates());
    }

    /**
     * Конструктор для создания объекта лабораторной работы.
     *
     * @param id уникальный идентификатор.
     * @param name название работы.
     * @param coordinates координаты.
     * @param creationDate дата создания.
     * @param minimalPoint минимальный балл.
     * @param description описание.
     * @param tunedInWorks количество работ.
     * @param difficulty сложность.
     * @param author автор.
     */
    public LabWork(Integer id, String name, Coordinates coordinates, LocalDateTime creationDate,
                   float minimalPoint, String description, Integer tunedInWorks, Difficulty difficulty, Person author) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.minimalPoint = minimalPoint;
        this.description = description;
        this.tunedInWorks = tunedInWorks;
        this.difficulty = difficulty;
        this.author = author;
    }

    /**
     * Возвращает строковое представление объекта лабораторной работы.
     *
     * @return строка с данными о ID, названии и баллах работы.
     */
    @Override
    public String toString() {
        return "Labwork[\n" +
                "  id = " + id + ",\n" +
                "  name = '" + name + "',\n" +
                "  minimalPoint = " + minimalPoint + ",\n" +
                "  difficulty = " + difficulty + ",\n" +
                "  coordinates = " + coordinates + ",\n" +
                "  creationDate = " + creationDate.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + ",\n" +
                "  description = '" + (description != null ? description : "null") + "',\n" +
                "  tunedInWorks = " + (tunedInWorks != null ? tunedInWorks : "null") + ",\n" +
                "  author = " + (author != null ? author.getName() : "null") + "\n" +
                "]";
    }

    /** @return уникальный идентификатор работы. */
    public Integer getId() {
        return id;
    }

    /** @return название работы. */
    public String getName() {
        return name;
    }

    /** @return минимальный балл. */
    public float getMinimalPoint() {
        return minimalPoint;
    }

    /** @return сложность работы. */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * @return координаты
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * @return имя автора
     */
    public Person getAuthor() {
        return author;
    }

    /**
     * Устанавливает уникальный ID (Используется в командах insert и update)
     * @param integer уникальный номер
     */
    public void setID(Integer integer) {
        this.id = integer;
    }
}