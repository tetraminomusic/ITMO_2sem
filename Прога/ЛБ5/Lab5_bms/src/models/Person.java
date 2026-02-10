package models;

public class Person {
    private String name;
    private java.time.LocalDateTime birthday;
    private float height;
    private Double weight;
    private String passportID; //Поле может быть null

    public Person(String name, java.time.LocalDateTime birthday, float height, Double weight, String passportID) {
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