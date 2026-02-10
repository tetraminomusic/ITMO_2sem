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


//TODO: добавить Javadoc

public class Main {
    public static void main(String[] args) {
        System.out.println("Добро пожаловать в предложение LabWork!");
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            StringsCompleter completer = new StringsCompleter(
                    "help", "info", "show", "insert", "update", "remove_key",
                    "clear", "save", "execute_script", "exit", "history", "gavrilovsay",
                    "group_counting_by_minimal","print_field_descending_minimal_point",
                    "remove_lover", "replace_if_greater", "count_less_than_difficulty", "polyakov", "write_ПСЖ");

            LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).completer(completer).build();

            CollectionManager collectionManager = new CollectionManager();
            FileManager fileManager = new FileManager("LAB_DATA_PATH");
            LabWorkAsker asker = new LabWorkAsker(lineReader);

            //загружаем файл
            var loadedData = fileManager.read();
            if (loadedData != null) collectionManager.getCollection().putAll(loadedData);

            CommandManager commandManager = new CommandManager(collectionManager, fileManager, asker, lineReader);

            //обработка ввода
            while (true) {
                String input;
                try {
                    input = lineReader.readLine("\u001B[32mКонсольТерминал@Что-то_заUмное:~₽ \u001B[0m");
                } catch (EndOfFileException e) {
                    System.out.println("\nЗавершение работы программы...");
                    break;
                } catch (UserInterruptException e) {
                    System.out.println("\u001B[33mНе ломайте программу бедного студента, пожалуйста\u001B[0m");
                    continue;
                }

                if (input == null || input.trim().isEmpty()) continue;
                commandManager.execute(input.trim());


            }

        } catch (Exception e) {
            System.err.println("Критическая \u001B[31mОшибка\u001B[0m: " + e.getMessage());
        }



    }


}
