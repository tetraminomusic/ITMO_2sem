package models;

import java.io.Serializable;

/**
 * Перечисление, представляющее уровни сложности лабораторной работы.
 * Используется для задания сложности объектов {@link LabWork}.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public enum Difficulty implements Serializable {
    //serialVersion не добавляем, так как сериализация реализована автоматически для enum
    /** Очень низкий уровень сложности. */
    VERY_EASY,

    /** Высокий уровень сложности. */
    HARD,

    /**
     *  Экстремальный уровень сложности.
     */
    HOPELESS;
}