public class LabWork {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private float minimalPoint; //Значение поля должно быть больше 0
    private String description; //Поле может быть null
    private Integer tunedInWorks; //Поле может быть null
    private Difficulty difficulty; //Поле не может быть null
    private Person author; //Поле может быть null
}
public class Coordinates {
    private Long x; //Максимальное значение поля: 162, Поле не может быть null
    private long y;
}
public class Person {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private java.time.LocalDateTime birthday; //Поле не может быть null
    private float height; //Значение поля должно быть больше 0
    private Double weight; //Поле может быть null, Значение поля должно быть больше 0
    private String passportID; //Длина строки не должна быть больше 50, Поле может быть null
}
public enum Difficulty {
    VERY_EASY,
    HARD,
    HOPELESS;
}
