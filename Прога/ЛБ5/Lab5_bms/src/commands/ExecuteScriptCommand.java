package commands;

import managers.CommandManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ExecuteScriptCommand implements Command{
    private final CommandManager commandManager;

    public ExecuteScriptCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void execute(String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("Ошибка: Введите имя файла-скрипта!");
            return;
        }

        File file = new File(arg);

        try (Scanner scriptScanner = new Scanner(file)) {
            commandManager.getAsker().setScriptScanner(scriptScanner);
            while (scriptScanner.hasNextLine()) {
                String line = scriptScanner.nextLine();
                if (line.isEmpty()) continue;

                commandManager.execute(line);
            }
            commandManager.getAsker().setScriptScanner(null);
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: Файл " + arg + " не был найден!");
        }
    }

    @Override
    public String getDescription() {
        return "Исполнение файла-скрипта";
    }
}
