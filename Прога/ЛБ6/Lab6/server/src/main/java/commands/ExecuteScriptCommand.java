package commands;

import managers.CommandManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Команда, которая исполняет файл-скрипт
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class ExecuteScriptCommand implements Command{
    /**
     * Менеджер команд, к которому идёт обращение при поиске подходящей команды из скрипта
     */
    private final CommandManager commandManager;
    /**
     * Множество для отслеживания выполняющихся скриптов (предотвращение рекурсии)
     */
    private static final Set<String> runningScripts = new HashSet<>();

    /**
     * Конструктор команды.
     * @param commandManager менеджер команд.
     */
    public ExecuteScriptCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * Выполнение логики команды. Переключает источник ввода Asker на файловый поток.
     * @param arg - путь к файлу-скрипту.
     */
    @Override
    public void execute(String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Введите имя файла-скрипта!");
            return;
        }

        File file = new File(arg);

        //проверка на права у файла скрипта

        //проверяем, существует ли файл
        if (!file.exists()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Файл " + arg + " не существует!");
            return;
        }

        //проверяем, является ли это файлом (а не директорией)
        if (!file.isFile()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: " + arg + " является директорией, а не файлом!");
            return;
        }

        //проверяем права на чтение
        if (!file.canRead()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Недостаточно прав для чтения файла " + arg + "!");
            return;
        }

        String absolutePath;
        try {
            absolutePath = file.getCanonicalPath();
        } catch (Exception e) {
            absolutePath = file.getAbsolutePath();
        }

        if (runningScripts.contains(absolutePath)) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Обнаружена рекурсия! Скрипт " + arg + " уже выполняется.");
            return;
        }

        runningScripts.add(absolutePath);


        //используем для автоматического закрытия сканера
        try (Scanner scriptScanner = new Scanner(file)) {
            //устанавливаем режим считывания данных на сканер заместо ридера,
            commandManager.getAsker().setScriptScanner(scriptScanner);
            while (scriptScanner.hasNextLine()) {
                String line = scriptScanner.nextLine();
                if (line.isEmpty()) continue;

                //проверка на скрипт
                if (line.trim().startsWith("execute_script")) {
                    String[] parts = line.trim().split("\\s+", 2);
                    if (parts.length > 1) {
                        String scriptName = parts[1];
                        File nestedFile = new File(scriptName);
                        String nestedPath;
                        try {
                            nestedPath = nestedFile.getCanonicalPath();
                        } catch (Exception e) {
                            nestedPath = nestedFile.getAbsolutePath();
                        }

                        //Проверяем, не пытаемся ли мы вызвать выполняющийся скрипт
                        if (runningScripts.contains(nestedPath)) {
                            System.out.println("\u001B[31mОшибка\u001B[0m: Обнаружена рекурсия! Скрипт " + scriptName + " уже выполняется.");
                            continue;
                        }
                    }
                }

                System.out.println("Выполнение: " + line);
                commandManager.execute(line);
            }
            commandManager.getAsker().setScriptScanner(null);



        } catch (FileNotFoundException e) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Файл " + arg + " не был найден!");
        } catch (Exception e) {
            System.out.println("\u001B[31mОшибка\u001B[0m при исполнении скрипта: " + e.getMessage());
        } finally {
            //возвращаем ввод на нормальный режим
            if (commandManager.getAsker() != null) {
                commandManager.getAsker().setScriptScanner(null);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Исполнение файла-скрипта";
    }
}
