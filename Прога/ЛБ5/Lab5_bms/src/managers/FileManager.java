package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import models.LabWork;

import java.io.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class FileManager {
    private String envVar;
    private Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    // первый метод - чтобы json файл был читаемый, второй - чтобы он создался

    public FileManager(String envVar) {
        this.envVar = envVar;
    }

    public void write(LinkedHashMap<String, LabWork> collection) {
        String path = System.getenv(envVar);

        if (path == null) {
            System.out.println("Ошибка: Переменная окружения " + envVar + " не установлена!");
            return;
        }

        File file = new File(path);
        File parentDir = file.getParentFile();


        if (file.exists()) {
            if (!file.canWrite()) {
                System.out.println("Ошибка: Недостаточно прав на запись в файл" + path);
                return;
            } else if (!file.isFile()) {
                System.out.println("Ошибка: В переменной окружения указан не файл: " + file.getName());
            }
        } else {
            if (parentDir.exists() && !parentDir.canWrite()) {
                System.out.println("Ошибка: В директории " + parentDir.getName() + " нельзя создать файл!");
                return;
            }
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path))) {
            String json = gson.toJson(collection);
            writer.write(json);
            System.out.println("Запись в файл произошла успешно");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении в файл: " + e.getMessage());
        }
    }

    public LinkedHashMap<String, LabWork> read() {
        String path = System.getenv(envVar);

        //TODO: хз, но возможно надо добавить проверку на наличие права на выполнения у родительской директории

        if (path == null || path.isEmpty()) {
            System.out.println("Ошибка: Переменная окружения " + envVar + " не установлена! Будет создана пустая коллекция");
            return new LinkedHashMap<>();
        }

        File file = new File(path);

        if (!file.exists()) {
            System.out.println("Ошибка: Такого файла не существует! Будет создана пустая коллекция");
            return new LinkedHashMap<String, LabWork>();
        }

        if (file.exists()) {
            if (!file.canRead()) {
                System.out.println("Ошибка: Недостаточно прав на чтение файла " + path + ". Будет создана пустая коллекция");
                return new LinkedHashMap<String, LabWork>();
            } else if (!file.isFile()) {
                System.out.println("Ошибка: В переменной окружения указан не файл: " + file.getName() + ". Будет создана пустая коллекция");
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
            System.out.println("Ошибка: Файл не был найден! Будет создана пустая коллекция");
            return new LinkedHashMap<String, LabWork>();
        }
    }


}
