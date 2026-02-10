package models;

public class LabWork implements Comparable<LabWork> {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private float minimalPoint; //Значение поля должно быть больше 0
    private String description; //Поле может быть null
    private Integer tunedInWorks; //Поле может быть null
    private Difficulty difficulty; //Поле не может быть null
    private Person author; //Поле может быть null

    //сортировка по умолчанию, ну пусть будет по имени

    @Override
    public int compareTo(LabWork other) {
        return this.name.compareTo(other.getName());
    }


    public LabWork(Integer id, String name, Coordinates coordinates, java.time.LocalDateTime creationDate,
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

    @Override
    public String toString() {
        return "Labwork[id=" + id + ", name='" + name + "', point=" + minimalPoint + "]";
    }



    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getMinimalPoint() {
        return minimalPoint;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
}