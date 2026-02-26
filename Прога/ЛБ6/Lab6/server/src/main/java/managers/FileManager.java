package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import models.LabWork;
import models.Person;
import network.LocalDateTimeAdapter;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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
     * Логгер для записи этапов работы с файлом
     */
    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);
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
            logger.error("Ошибка: Переменная окружения {} не установлена!", envVar);
            return;
        }

        File file = new File(path);
        File parentDir = file.getParentFile();


        if (file.exists()) {
            if (!file.canWrite()) {
                logger.error("Ошибка: Недостаточно прав на запись в файл: {}", file.getName());
                return;
            } else if (!file.isFile()) {
                logger.error("Ошибка: В переменной окружения указан не файл: {}", file.getName());
            }
        } else {
            if (parentDir.exists() && !parentDir.canWrite()) {
                logger.error("Ошибка: В директории {} нельзя создать файл!", file.getParentFile().getName());
                return;
            }
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path))) {
            String json = gson.toJson(collection);
            writer.write(json);
            logger.info("Запись в файл произошла успешно");
        } catch (IOException e) {
            logger.error("Ошибка: при сохранении в файл: {} ", e.getMessage());
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
            logger.error("Ошибка: Переменная окружения {} не установлена! Будет создана пустая коллекция", envVar);
            return new LinkedHashMap<>();
        }

        File file = new File(path);
        File parentDir = file.getParentFile();

        //проверка существования родительской директории
        if (parentDir != null && !parentDir.exists()) {
            logger.error("Ошибка: Директория {} не существует! Будет создана пустая коллекция", parentDir.getPath());
            return new LinkedHashMap<String, LabWork>();
        }

        //проверка прав на выполнение для родительской директории (нужно для доступа к файлу)
        if (parentDir != null && !parentDir.canExecute()) {
            logger.error("Ошибка: Недостаточно прав на доступ к директории {}! Будет создана пустая коллекция", parentDir.getPath());
            return new LinkedHashMap<String, LabWork>();
        }

        if (!file.exists()) {
            logger.error("Ошибка: Файл {} не существует! Будет создана пустая коллекция", path);
            return new LinkedHashMap<String, LabWork>();
        }

        if (!file.isFile()) {
            logger.error("Ошибка: Путь {} указывает не на файл, а на директорию! Будет создана пустая коллекция", path);
            return new LinkedHashMap<String, LabWork>();
        }

        if (!file.canRead()) {
            logger.error("Ошибка: Недостаточно прав на чтение файла {}! Будет создана пустая коллекция", path);
            return new LinkedHashMap<String, LabWork>();
        }

        if (file.length() == 0) {
            logger.warn("Предупреждение: Файл {} пустой! Будет создана пустая коллекция", path);
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
                logger.warn("Предупреждение: Файл пуст или содержит некорректные данные! Будет создана пустая коллекция");
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
                        logger.error("Ошибка валидации данных: Элемент {} (ключ: {}) равен null!", elementNumber, entry.getKey());
                        hasErrors = true;
                        continue;
                    }

                    Integer id = labWork.getId();

                    //проверка на id
                    if (id == null) {
                        logger.error("Ошибка валидации данных: У элемента {} (ключ: {}) id = null!", elementNumber, entry.getKey());
                        hasErrors = true;
                        continue;
                    }

                    if (id <= 0) {
                        logger.error("Ошибка валидации данных: У элемента {} id = {} (должен быть > 0)!", elementNumber, id);
                        hasErrors = true;
                        continue;
                    }

                    //проверка на уникальность
                    if (idSet.contains(id)) {
                        logger.error("Ошибка валидации данных: Обнаружен повторяющийся id = {} в файле!", id);
                        hasErrors = true;
                        continue;
                    }
                    idSet.add(id);

                    //проверка на имя
                    if (labWork.getName() == null || labWork.getName().trim().isEmpty()) {
                        logger.error("Ошибка валидации данных: У элемента с id = {} имя не может быть пустым!", id);
                        hasErrors = true;
                    }

                    //проверка координат
                    if (labWork.getCoordinates() == null) {
                        logger.error("Ошибка валидации данных: У элемента с id = {} coordinates = null!", id);
                        hasErrors = true;
                    }

                    //проверка minimalPoint > 0
                    if (labWork.getMinimalPoint() <= 0) {
                        logger.error("Ошибка валидации данных: У элемента с id = {} minimalPoint = {} (должен быть > 0)!", id, labWork.getMinimalPoint());
                        hasErrors = true;
                    }

                    if (labWork.getDifficulty() == null) {
                        logger.error("Ошибка валидации данных: У элемента с id = {} difficulty = null!", id);
                        hasErrors = true;
                    }

                    //проверка автора (если есть)
                    if (labWork.getAuthor() != null) {
                        Person author = labWork.getAuthor();
                        if (author.getName() == null || author.getName().trim().isEmpty()) {
                            logger.error("Ошибка валидации данных: У автора элемента с id = {} имя не может быть пустым!", id);
                            hasErrors = true;
                        }
                        if (author.getHeight() <= 0) {
                            logger.error("Ошибка валидации данных: У автора элемента с id = {} рост должен быть > 0!", id);
                            hasErrors = true;
                        }
                        if (author.getWeight() != null && author.getWeight() <= 0) {
                            logger.error("Ошибка валидации данных: У автора элемента с id = {} вес должен быть > 0!", id);
                            hasErrors = true;
                        }
                        if (author.getPassportID() != null && author.getPassportID().length() > 50) {
                            logger.error("Ошибка валидации данных: У автора элемента с id = {} паспорт не должен превышать 50 символов!", id);
                            hasErrors = true;
                        }
                    }
                }

                //проверяем после обработки всех лаб
                if (hasErrors) {
                    logger.error("Ошибка валидации данных: Обнаружены ошибки в файле. Будет создана пустая коллекция");
                    return new LinkedHashMap<String, LabWork>();
                }

                logger.info("Успех: Коллекция успешно загружена из файла. Загружено элементов: {}", result.size());
                return result;
            }
        } catch (FileNotFoundException e) {
            logger.error("Ошибка: Файл не был найден! Будет создана пустая коллекция");
            return new LinkedHashMap<String, LabWork>();
        } catch (com.google.gson.JsonSyntaxException e) {
            logger.error("Ошибка: Неверный формат JSON в файле! Будет создана пустая коллекция");
            return new LinkedHashMap<String, LabWork>();
        }
    }


}
