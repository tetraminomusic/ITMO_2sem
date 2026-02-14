package managers;

import models.Coordinates;
import models.Difficulty;
import models.LabWork;
import models.Person;
import org.jline.reader.LineReader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Класс, отвечающий за интерактивное чтение данных с консоли или из файла-скрипта.
 * Реализует валидацию вводимых данных.
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class LabWorkAsker {
    /** Считыватель JLine для работы с терминалом. */
    private final LineReader reader;
    /** Сканер для чтения команд из файла-скрипта. */
    private Scanner scriptScanner = null;
    /** Форматтер для ввода дат. */
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    /** ANSI-код для красного цвета ошибок. */
    private static final String RED = "\u001B[31m";
    /** ANSI-код для сброса цвета. */
    private static final String RESET = "\u001B[0m";

    /**
     * Конструктор опрашивателя.
     * @param reader объект LineReader для чтения из консоли.
     */
    public LabWorkAsker(LineReader reader) {
        this.reader = reader;
    }

    /**
     * Устанавливает сканер для чтения из скрипта.
     * Если передано null, опрашиватель переключается на чтение из консоли.
     *
     * @param scriptScanner сканер файла-скрипта.
     */
    public void setScriptScanner(Scanner scriptScanner) {
        this.scriptScanner = scriptScanner;
    }

    /**
     * Базовый метод для чтения строки. Проверяет источник ввода (консоль или скрипт).
     *
     * @param message сообщение-приглашение для пользователя.
     * @param nullable флаг, разрешающий возврат null при пустом вводе.
     * @return считанная строка или null.
     */
    private String askString(String message, boolean nullable) {
        if (scriptScanner != null && scriptScanner.hasNextLine()) {
            String res = scriptScanner.nextLine().trim();
            System.out.println(message + " " + res); // Визуализация чтения из скрипта
            return (res.isEmpty() && nullable) ? null : res;
        }

        while (true) {
            String input = reader.readLine(message + " ").trim();
            if (input.isEmpty()) {
                if (nullable) return null;
                System.out.println(RED + "Ошибка: Поле не может быть пустым!" + RESET);
                continue;
            }
            return input;
        }
    }

    /**
     * Запрашивает число с плавающей точкой (float).
     * @param message сообщение для пользователя.
     * @param nullable разрешен ли null (вернет 0.0f).
     * @return значение типа float.
     */
    private float askFloat(String message, boolean nullable) {
        while (true) {
            try {
                String input = askString(message, nullable);
                if (input == null) return 0.0f;
                float val = Float.parseFloat(input);
                if (val <= 0) {
                    System.out.println(RED + "Ошибка: Число должно быть положительным!" + RESET);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println(RED + "Ошибка: Введите число (дробь через точку)!" + RESET);
            }
        }
    }

    /**
     * Запрашивает сложность из перечисления {@link Difficulty}.
     * @return константа сложности.
     */
    public Difficulty askDifficulty() {
        while (true) {
            System.out.println("Доступные сложности: " + Arrays.toString(Difficulty.values()));
            String line = askString("Выберите сложность:", false).toUpperCase();
            try {
                return Difficulty.valueOf(line);
            } catch (IllegalArgumentException e) {
                System.out.println(RED + "Ошибка: Такой сложности нет в списке!" + RESET);
            }
        }
    }

    /**
     * Запрашивает минимальный балл для лабораторной работы.
     * @return минимальный балл (>0).
     */
    public float askMinimalPoint() {
        return askFloat("Введите минимальный балл (> 0):", false);
    }

    /**
     * Запрашивает целое число типа Long в заданном диапазоне.
     * @param message приглашение к вводу.
     * @param min минимальное значение (null если не ограничено).
     * @param max максимальное значение (null если не ограничено).
     * @param nullable разрешен ли пустой ввод.
     * @return значение Long или null.
     */
    private Long askLong(String message, Long min, Long max, boolean nullable) {
        while (true) {
            try {
                String input = askString(message, nullable);
                if (input == null) return null;
                long val = Long.parseLong(input);
                if (min != null && val < min) throw new NumberFormatException();
                if (max != null && val > max) throw new NumberFormatException();
                return val;
            } catch (NumberFormatException e) {
                System.out.println(RED + "Ошибка: Введите целое число в правильном диапазоне!" + RESET);
            }
        }
    }

    /**
     * Запрашивает дату в формате ДД.ММ.ГГГГ.
     * @param message приглашение к вводу.
     * @return объект LocalDateTime (время установлено на начало дня).
     */
    private LocalDateTime askDate(String message) {
        while (true) {
            try {
                String input = askString(message, false);
                return LocalDate.parse(input, formatter).atStartOfDay();
            } catch (DateTimeParseException e) {
                System.out.println(RED + "Ошибка: формат даты должен быть ДД.ММ.ГГГГ (напр. 01.01.2000)" + RESET);
            }
        }
    }

    /**
     * Запрашивает целое число типа Integer.
     * @param message приглашение.
     * @param nullable разрешен ли null.
     * @return значение Integer или null.
     */
    private Integer askInt(String message, boolean nullable) {
        while (true) {
            try {
                String input = askString(message, nullable);
                if (input == null) return null;
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(RED + "Ошибка: Введите целое число!" + RESET);
            }
        }
    }

    /**
     * Запрашивает вес автора.
     * @param message приглашение.
     * @param nullable разрешен ли null.
     * @return значение Double (>0) или null.
     */
    private Double askWeight(String message, boolean nullable) {
        while (true) {
            try {
                String input = askString(message, nullable);
                if (input == null) return null;
                Double weight = Double.parseDouble(input);
                if (weight <= 0) {
                    System.out.println(RED + "Ошибка: Вес должен быть положительным!" + RESET);
                    continue;
                }
                return weight;
            } catch (NumberFormatException e) {
                System.out.println(RED + "Ошибка: Введите число (напр. 75.5)!" + RESET);
            }
        }
    }

    /**
     * Запрашивает паспортные данные.
     * @param message приглашение.
     * @param nullable разрешен ли null.
     * @return строка паспорта (не длиннее 50 символов).
     */
    private String askPassport(String message, boolean nullable) {
        while (true) {
            String passport = askString(message, nullable);
            if (passport != null && passport.length() > 50) {
                System.out.println(RED + "Ошибка: длина ID не должна превышать 50 символов!" + RESET);
                continue;
            }
            return passport;
        }
    }

    /**
     * Запускает процесс создания объекта {@link Coordinates}.
     * @return заполненный объект координат.
     */
    public Coordinates askCoordinates() {
        System.out.println("Ввод координат:");
        Long x = askLong(" Введите X (макс. 162):", null, 162L, false);
        Long y = askLong(" Введите Y:", null, null, false);
        return new Coordinates(x, y);
    }

    /**
     * Запускает процесс создания объекта {@link Person}.
     * Позволяет пропустить ввод автора, если на имени будет нажат Enter.
     *
     * @return заполненный объект автора или null.
     */
    public Person askPerson() {
        System.out.println("--- Ввод данных автора (Enter на имени, чтобы пропустить) ---");
        String name = askString(" Имя автора:", true);
        if (name == null) return null;

        LocalDateTime birthday = askDate(" Дата рождения (ДД.ММ.ГГГГ):");
        float height = askFloat(" Рост автора:", false);
        Double weight = askWeight(" Вес автора:", true);
        String passportID = askPassport(" ID паспорта:", true);

        return new Person(name, birthday, height, weight, passportID);
    }

    /**
     * Собирает полный объект {@link LabWork}, запрашивая все необходимые поля.
     *
     * @param id уникальный идентификатор для новой работы.
     * @return полностью инициализированный объект лабораторной работы.
     */
    public LabWork createLabWork(Integer id) {
        System.out.println("--- Создание лабораторной работы ---");
        String name = askString("Название лабораторной:", false);
        Coordinates coord = askCoordinates();
        float minPoint = askMinimalPoint();
        String description = askString("Описание (опционально):", true);
        Integer tunedInWorks = askInt("Количество TunedInWorks (опционально):", true);
        Difficulty difficulty = askDifficulty();
        Person person = askPerson();

        return new LabWork(
                id,
                name,
                coord,
                LocalDateTime.now(),
                minPoint,
                description,
                tunedInWorks,
                difficulty,
                person
        );
    }
}