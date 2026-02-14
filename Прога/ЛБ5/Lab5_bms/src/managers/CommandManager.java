package managers;

import commands.*;
import commands.memes.GavrilovsayCommand;
import commands.memes.PolyakovCommand;
import commands.memes.WriteDeduction;
import org.jline.reader.LineReader;

import java.util.*;

/**
 * Менеджер команд, который управляет работой всех команд консольного приложения.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class CommandManager {
    /**
     * Список, который будет хранить все команды.
     */
    private final Map<String, Command> commands = new LinkedHashMap<>();
    /**
     * Список, хранящий историю последних 14 использованных команд.
     */
    private final List<String> history = new LinkedList<>();
    /**
     * Интерфейс запроса данных для создания нового элемента коллекции.
     */
    private final LabWorkAsker asker;
    /**
     * Интерфейс считывания данных, используемый для команды Execute_Script.
     */
    private final LineReader reader;

    /**
     * Конструктор менеджера команд.
     * @param collection коллекция элементов.
     * @param file имя файла, в которой будет происходить загрузка коллекции.
     * @param asker интерфейс запроса данных для последующего создания нового элемента для коллекции.
     * @param reader интерфейс считывания данных, используемый командой Execute_Script.
     */
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
        commands.put("exit", new ExitCommand());
        commands.put("remove_lower", new RemoveLowerCommand(collection, asker));
        commands.put("history", new HistoryCommand(history));
        commands.put("replace_if_greater", new ReplaceIfGreaterCommand(collection, asker));
        commands.put("group_counting_by_minimal_point", new GroupCountingByMinimalCommand(collection));
        commands.put("count_less_difficulty", new CountLessThanDifficulty(collection));
        commands.put("print_field_desceding_minimal_point", new PrintFieldDescendingMinimalPointCommand(collection));

        //рофло-команды
        commands.put("gavrilovsay", new GavrilovsayCommand());
        commands.put("polyakov", new PolyakovCommand());
        commands.put("псж", new WriteDeduction(reader));
    }

    /**
     * Определение и последующий запрос на выполнение команды.
     * @param input имя команды и аргумент команды, если такой имеется.
     */
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

    /**
     * Добавляет команду в историю команд.
     * @param cmd команда.
     */
    private void addToHistory(String cmd) {
        history.add(cmd);
        if (history.size() > 14) {
            history.remove(0);
        }
    }

    /**
     * Получение истории команд.
     * @return история команд.
     */
    public List<String> getHistory() {
        return history;
    }

    /**
     * Получение всех имён команд.
     * @return список всех команд.
     */
    public Set<String> getCommandNames() {
        return commands.keySet();
    }

    /**
     * Получение интерфейса запроса данных для создания нового элемента в коллекции.
     * @return интерфейс запроса данных.
     */
    public LabWorkAsker getAsker() {
        return asker;
    }

}
