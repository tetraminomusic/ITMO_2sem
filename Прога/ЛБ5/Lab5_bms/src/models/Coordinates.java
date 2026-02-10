package models;

public class Coordinates {
    private Long x; //Максимальное значение поля: 162, Поле не может быть null
    private long y;

    public Coordinates(Long x, long y) {
        if (x == null || x > 162) {
            throw new IllegalArgumentException("Значение переменной x не должен превышать 162-х");
        }
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y;
    }
}