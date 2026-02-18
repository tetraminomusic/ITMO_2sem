package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import models.LabWork;

import java.io.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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

        //TODO: хз, но возможно надо добавить проверку на наличие права на выполнения у родительской директории

        if (path == null || path.isEmpty()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Переменная окружения " + envVar + " не установлена! Будет создана пустая коллекция");
            return new LinkedHashMap<>();
        }

        File file = new File(path);

        if (!file.exists()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Такого файла не существует! Будет создана пустая коллекция");
            return new LinkedHashMap<String, LabWork>();
        } else {
            if (!file.canRead()) {
                System.out.println("\u001B[31mОшибка\u001B[0m: Недостаточно прав на чтение файла " + path + ". Будет создана пустая коллекция");
                return new LinkedHashMap<String, LabWork>();
            } else if (!file.isFile()) {
                System.out.println("\u001B[31mОшибка\u001B[0m: В переменной окружения указан не файл: " + file.getName() + ". Будет создана пустая коллекция");
                return new LinkedHashMap<String, LabWork>();
            }
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
                return new LinkedHashMap<String, LabWork>();
            } else {
                return result;
            }
        } catch (FileNotFoundException e) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Файл не был найден! Будет создана пустая коллекция");
            return new LinkedHashMap<String, LabWork>();
        }
    }


}
