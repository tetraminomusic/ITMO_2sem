package managers;

import commands.*;
import org.jline.reader.LineReader;

import java.util.*;

public class CommandManager {
    // создаём map, состоящих из команд
    private final Map<String, Command> commands = new LinkedHashMap<>();

    private final List<String> history = new LinkedList<>();

    private final LabWorkAsker asker;

    private final LineReader reader;

    public CommandManager(CollectionManager collection, FileManager file, LabWorkAsker asker, LineReader reader) {

        this.asker = asker;
        this.reader = reader;

        commands.put("help", new HelpCommand(commands));
        commands.put("info", new InfoCommand(collection));
        commands.put("show", new ShowCommand(collection));
        commands.put("insert", new InsertCommand(collection, asker));
        commands.put("update", new UpdateCommand(collection, asker));
        commands.put("remove_key", new RemovekeyCommand(collection));
        commands.put("clear", new ClearCommand(collection));
        commands.put("save", new SaveCommand(collection, file));
        commands.put("execute_script", new ExecuteScriptCommand(this));
        commands.put("exit", new ExitCommand(collection));
        commands.put("remove_lower", new RemoveLowerCommand(collection, asker));
        commands.put("history", new HistoryCommand(history));
        commands.put("replace_if_greater", new ReplaceIfGreaterCommand(collection, asker));
        commands.put("group_counting_by_minimal", new GroupCountingByMinimalCommand(collection));
        commands.put("count_less_difficulty", new CountLessThanDifficulty(collection));
        commands.put("print_field_desceding_minimal_point", new PrintFieldDescendingMinimalPointCommand(collection));

        commands.put("gavrilovsay", new GavrilovsayCommand(collection));
        commands.put("polyakov", new PolyakovCommand(collection));
        commands.put("псж", new WriteDeduction(reader));
    }

    public void execute(String input) {
        if (input.trim().isEmpty()) return;

        String[] tokens = input.split("\\s+", 2);

        String pre_cmd = tokens[0].toLowerCase();

        String arg;
        if (tokens.length > 1) arg = tokens[1];
        else arg = null;

        Command command = commands.get(pre_cmd);

        if (command != null) {
            addToHistory(pre_cmd);
            command.execute(arg);
        } else {
            System.out.println("\u001B[31mОшибка\u001B[0m: Команда " + pre_cmd + " не была найдена! Введите команду 'help' для справки");
        }

    }

    private void addToHistory(String cmd) {
        history.add(cmd);
        if (history.size() > 14) {
            history.remove(0);
        }
    }

    public List<String> getHistory() {
        return history;
    }

    public Set<String> getCommandNames() {
        return commands.keySet();
    }

    public LabWorkAsker getAsker() {
        return asker;
    }

}
