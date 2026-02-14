package commands;

import managers.CommandManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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

        //используем для автоматического закрытия сканера
        try (Scanner scriptScanner = new Scanner(file)) {
            //устанавливаем режим считывания данных на сканер за место ридера,
            commandManager.getAsker().setScriptScanner(scriptScanner);
            while (scriptScanner.hasNextLine()) {
                String line = scriptScanner.nextLine();
                if (line.isEmpty()) continue;

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
