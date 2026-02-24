package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import models.LabWork;
import models.Person;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Менеджер по взаимодействию с файлами. Например, запись или чтение.
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class FileManager {
    /**
     * Переменная окружения
     */
    private String envVar;
    /**
     * Парсер для чтения и записи данных файла формата json.
     */
    private Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    // первый метод - чтобы json файл был читаемый, второй - чтобы он создался

    /**
     * Конструктор менеджера файлов.
     * @param envVar переменная окружения.
     */
    public FileManager(String envVar) {
        this.envVar = envVar;
    }

    /**
     * Записывает коллекцию в файл
     * Предварительно проводит сериализацию коллекции в формат json.
     * @param collection сама коллекция, которую хотим записать в файл.
     */
    public void write(LinkedHashMap<String, LabWork> collection) {
        String path = System.getenv(envVar);

        if (path == null) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Переменная окружения " + envVar + " не установлена!");
            return;
        }

        File file = new File(path);
        File parentDir = file.getParentFile();


        if (file.exists()) {
            if (!file.canWrite()) {
                System.out.println("\u001B[31mОшибка\u001B[0m: Недостаточно прав на запись в файл" + path);
                return;
            } else if (!file.isFile()) {
                System.out.println("\u001B[31mОшибка\u001B[0m: В переменной окружения указан не файл: " + file.getName());
            }
        } else {
            if (parentDir.exists() && !parentDir.canWrite()) {
                System.out.println("\u001B[31mОшибка\u001B[0m: В директории " + parentDir.getName() + " нельзя создать файл!");
                return;
            }
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path))) {
            String json = gson.toJson(collection);
            writer.write(json);
            System.out.println("Запись в файл произошла успешно");
        } catch (IOException e) {
            System.out.println("\u001B[31mОшибка\u001B[0m при сохранении в файл: " + e.getMessage());
        }
    }

    /**
     * Чтение коллекции из файла. Производится автоматически при запуске консольного приложения.
     * Проводит десериализацию данных json формата в коллекцию элементов.
     * @return коллекцию, которая считалась из файла.
     */
    public LinkedHashMap<String, LabWork> read() {
        String path = System.getenv(envVar);

        if (path == null || path.isEmpty()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Переменная окружения " + envVar + " не установлена! Будет создана пустая коллекция");
            return new LinkedHashMap<>();
        }

        File file = new File(path);
        File parentDir = file.getParentFile();

        //проверка существования родительской директории
        if (parentDir != null && !parentDir.exists()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Директория " + parentDir.getPath() + " не существует! Будет создана пустая коллекция");
            return new LinkedHashMap<String, LabWork>();
        }

        //проверка прав на выполнение для родительской директории (нужно для доступа к файлу)
        if (parentDir != null && !parentDir.canExecute()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Недостаточно прав на доступ к директории " + parentDir.getPath() + "! Будет создана пустая коллекция");
            return new LinkedHashMap<String, LabWork>();
        }

        if (!file.exists()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Файл " + path + " не существует! Будет создана пустая коллекция");
            return new LinkedHashMap<String, LabWork>();
        }

        if (!file.isFile()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Путь " + path + " указывает не на файл, а на директорию! Будет создана пустая коллекция");
            return new LinkedHashMap<String, LabWork>();
        }

        if (!file.canRead()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Недостаточно прав на чтение файла " + path + "! Будет создана пустая коллекция");
            return new LinkedHashMap<String, LabWork>();
        }

        if (file.length() == 0) {
            System.out.println("\u001B[33mПредупреждение\u001B[0m: Файл " + path + " пустой! Будет создана пустая коллекция");
            return new LinkedHashMap<String, LabWork>();
        }

        try (Scanner scanner = new Scanner(file)) {
            StringBuilder jsonString = new StringBuilder();
            while (scanner.hasNextLine()) {
                jsonString.append(scanner.nextLine());
            }
            // Говорим GSON, какой именно тип данных мы ждём
            var type = new TypeToken<LinkedHashMap<String, LabWork>>(){}.getType();

            //указываем строку и тип данных, в который нужно провести сереализацию
            LinkedHashMap<String, LabWork> result = gson.fromJson(jsonString.toString(), type);

            if (result == null) {
                System.out.println("\u001B[33mПредупреждение\u001B[0m: Файл пуст или содержит некорректные данные! Будет создана пустая коллекция");
                return new LinkedHashMap<String, LabWork>();
            } else {
                HashSet<Integer> idSet = new HashSet<>();
                boolean hasErrors = false;
                int elementNumber = 0;

                for (Map.Entry<String, LabWork> entry : result.entrySet()) {
                    elementNumber++;
                    LabWork labWork = entry.getValue();

                    //пропуск null лаб
                    if (labWork == null) {
                        System.out.println("\u001B[31mОшибка валидации данных\u001B[0m: Элемент " + elementNumber + " (ключ: " + entry.getKey() + ") равен null!");
                        hasErrors = true;
                        continue;
                    }

                    Integer id = labWork.getId();

                    //проверка на id
                    if (id == null) {
                        System.out.println("\u001B[31mОшибка валидации данных\u001B[0m: У элемента " + elementNumber + " (ключ: " + entry.getKey() + ") id = null!");
                        hasErrors = true;
                        continue;
                    }

                    if (id <= 0) {
                        System.out.println("\u001B[31mОшибка валидации данных\u001B[0m: У элемента " + elementNumber + " id = " + id + " (должен быть > 0)!");
                        hasErrors = true;
                        continue;
                    }

                    //проверка на уникальность
                    if (idSet.contains(id)) {
                        System.out.println("\u001B[31mОшибка валидации данных\u001B[0m: Обнаружен повторяющийся id = " + id + " в файле!");
                        hasErrors = true;
                        continue;
                    }
                    idSet.add(id);

                    //проверка на имя
                    if (labWork.getName() == null || labWork.getName().trim().isEmpty()) {
                        System.out.println("\u001B[31mОшибка валидации данных\u001B[0m: У элемента с id = " + id + " имя не может быть пустым!");
                        hasErrors = true;
                    }

                    //проверка координат
                    if (labWork.getCoordinates() == null) {
                        System.out.println("\u001B[31mОшибка валидации данных\u001B[0m: У элемента с id = " + id + " coordinates = null!");
                        hasErrors = true;
                    }

                    //проверка minimalPoint > 0
                    if (labWork.getMinimalPoint() <= 0) {
                        System.out.println("\u001B[31mОшибка валидации данных\u001B[0m: У элемента с id = " + id + " minimalPoint = " + labWork.getMinimalPoint() + " (должен быть > 0)!");
                        hasErrors = true;
                    }

                    if (labWork.getDifficulty() == null) {
                        System.out.println("\u001B[31mОшибка валидации данных\u001B[0m: У элемента с id = " + id + " difficulty = null!");
                        hasErrors = true;
                    }

                    //проверка автора (если есть)
                    if (labWork.getAuthor() != null) {
                        Person author = labWork.getAuthor();
                        if (author.getName() == null || author.getName().trim().isEmpty()) {
                            System.out.println("\u001B[31mОшибка валидации данных\u001B[0m: У автора элемента с id = " + id + " имя не может быть пустым!");
                            hasErrors = true;
                        }
                        if (author.getHeight() <= 0) {
                            System.out.println("\u001B[31mОшибка валидации данных\u001B[0m: У автора элемента с id = " + id + " рост должен быть > 0!");
                            hasErrors = true;
                        }
                        if (author.getWeight() != null && author.getWeight() <= 0) {
                            System.out.println("\u001B[31mОшибка валидации данных\u001B[0m: У автора элемента с id = " + id + " вес должен быть > 0!");
                            hasErrors = true;
                        }
                        if (author.getPassportID() != null && author.getPassportID().length() > 50) {
                            System.out.println("\u001B[31mОшибка валидации данных\u001B[0m: У автора элемента с id = " + id + " паспорт не должен превышать 50 символов!");
                            hasErrors = true;
                        }
                    }
                }

                //проверяем после обработки всех лаб
                if (hasErrors) {
                    System.out.println("\u001B[31mОшибка валидации данных\u001B[0m: Обнаружены ошибки в файле. Будет создана пустая коллекция");
                    return new LinkedHashMap<String, LabWork>();
                }

                //ошибок нет, возвращаем резы
                System.out.println("\u001B[32mУспех\u001B[0m: Коллекция успешно загружена из файла. Загружено элементов: " + result.size());
                return result;
            }
        } catch (FileNotFoundException e) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Файл не был найден! Будет создана пустая коллекция");
            return new LinkedHashMap<String, LabWork>();
        } catch (com.google.gson.JsonSyntaxException e) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Неверный формат JSON в файле! Будет создана пустая коллекция");
            return new LinkedHashMap<String, LabWork>();
        }
    }


}
