package managers;


import models.Coordinates;
import models.Difficulty;
import models.LabWork;
import models.Person;
import org.jline.reader.LineReader;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

public class LabWorkAsker {
    private final LineReader reader;
    private Scanner scriptScanner = null;

    public LabWorkAsker(LineReader reader) {
        this.reader = reader;
    }

    // метод переключатель - показывает, когда мы должны использовать Jline, а когда scanner
    public void setScriptScanner(Scanner scriptScanner) {
        this.scriptScanner = scriptScanner;
    }

    //Обычная проверка на то, чтобы хотя бы что-то да вводим
    private String askString(String message, boolean nullable) {

        if (scriptScanner != null && scriptScanner.hasNextLine()) {
            String res = scriptScanner.nextLine().trim();
            //покажем, что мы считали из файла
            System.out.println(message + " " + res);
            if (res.isEmpty() && nullable) {
                return null;
            } else {
                return res;
            }
        }


        while (true) {
            String input = reader.readLine(message + " ").trim();

            if (input.isEmpty()) {
                if (nullable) return null;
                System.out.println("Ошибка: Поле не может быть пустым!");
                continue;
            }
            return input;
        }
    }

    private float askFloat(String message, boolean nullable) {
        while (true) {
            try {
                String input = askString(message, nullable);
                //TODO; .replace(",",".");
                if (input == null) return 0.0f;
                float val = Float.parseFloat(input);
                if (val <= 0) {
                    System.out.println("Ошибка: Число должно быть неотрицательным!");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка:  введите число (дробь через точку)!");
            }
        }
    }

    public Difficulty askDifficulty() {
        while (true) {
            System.out.println("Доступные сложности: " + Arrays.toString(Difficulty.values()));
            String line =  reader.readLine("Выберите сложность: ").trim().toUpperCase();
            try {
                return Difficulty.valueOf(line);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: Такой сложности нет в списке!");
            }
        }
    }

    public float askMinimalPoint() {
        while(true) {
            try {
                String line = reader.readLine("Введите минимальный балл: ").trim();
                float point = Float.parseFloat(line);

                if (point <= 0) {
                    System.out.println("Ошибка: Количество баллов должно быть неотрицательным!");
                    continue;
                }
                return point;
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: Введите число (дробь через точку)!");
            }
        }
    }



    //проверка на ввод числа (обращаемся опять-таки к методу выше)
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
                System.out.println("Обишка! Введите целое число в правильном диапазоне!");
            }
        }
    }

    private LocalDateTime askDate(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        while (true) {
            try {
                String input = askString(message, false);
                return LocalDate.parse(input, formatter).atStartOfDay();
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: формат даты должен быть ДД.ММ.ГГГГ (например, 01.01.2000)");
            }
        }
    }

    private Integer askInt(String message, boolean nullable) {
        while (true) {
            try {
                String input = askString(message, nullable);
                if (input == null) return null;
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Введите целое число!");
            }
        }
    }


    private Double askWeight(String message, boolean nullable) {
        while (true) {
            String input = askString(message, nullable);
            if (input == null) return null;

            try {
                Double weight = Double.parseDouble(input);
                if (weight <= 0) {
                    System.out.println("Ошибка: Вес должен быть неотрицательным!");
                    continue;
                }
                return weight;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число (Например: 75.5)!");
            }
        }
    }


    private String askPassport(String message, boolean nullable) {
        while (true) {
            String passport = askString(message, nullable);
            if (passport == null) return null;

            if (passport.length() > 50) {
                System.out.println("Ошибка: длина ID не должна превышать 50 символов!");
                continue;
            }
            return passport;
        }
    }


    //запрос на ввод координат
    public Coordinates askCoordinates() {
        System.out.println("Ввод координат:");
        Long x = askLong("Введите координату 'x' (Максимальное значение - 162): ", null, 162L, false);
        Long y = askLong("Введите координату 'y': ", null, null, false);
        return new Coordinates(x,y);
    }

    //запрос на ввод данных автора
    public Person askPerson() {

        //имя
        String name = askString("Введите имя автора (опционально):", false);
        if (name == null) return null;

        //дата рождения
        LocalDateTime birthday = askDate("Введите дату рождения (ДД.ММ.ГГГГ):");

        //рост
        Float height = askFloat("Введите рост автора:", false);

        //вес
        Double weight = askWeight("Введите вес автора (опционально):", true);

        //айди паспорта
        String passportID = askPassport("Введите ID паспорта (опционально):", true);

        return new Person(name, birthday, height, weight,passportID);

    }


    public LabWork createLabWork(Integer id) {
        System.out.println("Создание лабораторной работы: ");
        //имя
        String name = askString("Введите название лабораторной: ", false);

        //координаты
        Coordinates coord = askCoordinates();

        //минимальный балл
        float minPoint = askMinimalPoint();

        String description = askString("Введите описание работы (опционально) :", true);

        //TunedInWorks
        Integer tunedInWorks = askInt("Введите количество TunedInWorks (опционально): ", true);

        //сложность
        Difficulty difficulty = askDifficulty();

        //автор
        Person person = askPerson();

        return new LabWork(id, name, coord, LocalDateTime.now(), minPoint, description, tunedInWorks, difficulty, person);
    }

}
