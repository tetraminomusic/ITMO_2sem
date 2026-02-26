package models;

import java.io.Serializable;

/**
 * Класс координат объекта.
 * Содержит данные о положении лабораторной работы в пространстве.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class Coordinates implements Serializable {

    private static final long serialVersionUID = 1234L;
    /**
     * Координата X.
     * Максимальное значение поля: 162. Поле не может быть null.
     */
    private Long x;

    /**
     * Координата Y.
     */
    private long y;

    /**
     * Конструктор объекта координат.
     *
     * @param x координата X (максимум 162, не null).
     * @param y координата Y.
     * @throws IllegalArgumentException если координата x не соответствует ограничениям.
     */
    public Coordinates(Long x, long y) {
        if (x == null || x > 162) {
            throw new IllegalArgumentException("Значение переменной x не должно превышать 162-х и не может быть null");
        }
        this.x = x;
        this.y = y;
    }

    /**
     * Возвращает строковое представление координат для вывода пользователю.
     *
     * @return строка формата "x=значение, y=значение".
     */
    @Override
    public String toString() {
        return "x=" + x + ", y=" + y;
    }

    /**
     * Метод, использующийся для сортировки объектов внутри коллекций по местоположению (по координатам)
     * @param other
     * @return
     */
    public int compareTo(Coordinates other) {
        if (other == null) return 1;

        double distThis = Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));

        double distOther = Math.sqrt(Math.pow(other.x, 2) + Math.pow(other.y, 2));

        return Double.compare(distThis, distOther);
    }
}