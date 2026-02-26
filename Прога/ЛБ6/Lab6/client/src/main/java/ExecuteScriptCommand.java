
import managers.LabWorkAsker;
import managers.UDPClient;
import network.Request;
import network.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Команда, которая исполняет файл-скрипт
 * Переехала сюда прямиком из сервера
 * @author Малых Кирилл Романович
 * @version 1.1
 */
public class ExecuteScriptCommand{
    private final UDPClient udpClient;
    private final LabWorkAsker asker;
    private final List<String> objectCommands;
    /**
     * Множество для отслеживания выполняющихся скриптов (предотвращение рекурсии)
     */
    private static final Set<String> runningScripts = new HashSet<>();

    /**
     * Конструктор команды.
     * @param commandManager менеджер команд.
     */
    public ExecuteScriptCommand(UDPClient udpClient, LabWorkAsker asker, List<String> objectCommands) {
        this.udpClient = udpClient;
        this.asker = asker;
        this.objectCommands = objectCommands;
    }

    /**
     * Выполнение логики команды. Переключает источник ввода Asker на файловый поток.
     * @param path - путь к файлу-скрипту.
     */
    public void execute(String path) {
        if (path == null || path.isEmpty()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Введите имя файла-скрипта!");
            return;
        }

        File file = new File(path);
        String absolutePath = file.getAbsolutePath();

        if (runningScripts.contains(absolutePath)) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Рекурсия! Скрипт '" + path + "' пропущен.");
            return;
        }

        runningScripts.add(absolutePath);

        try (Scanner scriptScanner = new Scanner(file)) {
            System.out.println("Начало выполнения скрипта: " + path);

            while (scriptScanner.hasNextLine()) {
                String line = scriptScanner.nextLine().trim();
                if (line.isEmpty()) continue;

                // Разбираем строку из файла
                String[] tokens = line.split("\\s+", 2);
                String command = tokens[0].toLowerCase();
                String argument = (tokens.length > 1) ? tokens[1] : "";

                // Если внутри скрипта встретился другой скрипт
                if (command.equals("execute_script")) {
                    execute(argument); // Рекурсивный вызов этого же метода
                    continue;
                }

                // Игнорируем локальные команды
                if (command.equals("exit") || command.equals("save")) continue;

                // Формируем запрос для сервера
                Request request;
                if (objectCommands.contains(command)) {
                    // Переключаем Asker на сканер файла
                    asker.setScriptScanner(scriptScanner);
                    try {
                        models.LabWork lab = asker.createLabWork(0);
                        request = new Request(command, argument, lab);
                    } finally {
                        asker.setScriptScanner(null); // Возвращаем ввод на консоль
                    }
                } else {
                    request = new Request(command, argument, null);
                }

                // Отправляем на сервер
                try {
                    udpClient.sendRequest(request);
                    Response response = udpClient.receiveResponse();
                    System.out.println("Скрипт [" + path + "]: " + response.getMessage());
                } catch (Exception e) {
                    System.err.println("Ошибка сети в скрипте: " + e.getMessage());
                }
            }

            System.out.println("Выполнение скрипта '" + path + "' завершено.");

        } catch (FileNotFoundException e) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Файл не найден: " + path);
        } finally {
            runningScripts.remove(absolutePath);
        }
    }

    /**
     * Возвращает строковое описание команды
     */
    public String getDescription() {
        return "Исполнение файла-скрипта";
    }
}
