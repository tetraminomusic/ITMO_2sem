import managers.CollectionManager;
import managers.CommandManager;
import managers.FileManager;
import managers.LabWorkAsker;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Main {
    public static void main(String[] args) {
        System.out.println("Добро пожаловать в предложение LabWork!");
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            StringsCompleter completer = new StringsCompleter("help", "info", "show", "insert", "update", "remove_key", "clear", "save", "execute_script", "exit", "history", "gavrilovsay");
            LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).completer(completer).build();



            CollectionManager collectionManager = new CollectionManager();
            FileManager fileManager = new FileManager("LAB_DATA_PATH");
            LabWorkAsker asker = new LabWorkAsker(lineReader);

            //загружаем файл
            var loadedData = fileManager.read();
            if (loadedData != null) collectionManager.getCollection().putAll(loadedData);

            CommandManager commandManager = new CommandManager(collectionManager, fileManager, asker);

            //обработка ввода
            while (true) {
                String input;
                try {
                    input = lineReader.readLine("₽ ");
                } catch (EndOfFileException e) {
                    System.out.println("\nЗавершение работы программы...");
                    break;
                } catch (UserInterruptException e) {
                    System.out.println("Не ломайте программу бедного студента, пожалуйста");
                    continue;
                }

                if (input == null || input.trim().isEmpty()) continue;
                commandManager.execute(input.trim());


            }

        } catch (Exception e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
        }



    }


}
