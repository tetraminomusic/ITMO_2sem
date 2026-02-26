package managers;

import commands.*;
import commands.memes.GavrilovsayCommand;
import commands.memes.PolyakovCommand;
import network.Request;
import network.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Менеджер команд для серверной части.
 * Принимает объект Request, находит нужную команду и возвращает Response.
 */
public class CommandManager {
    /**
     * Логгер для сетевого менеджера
     */
    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

    /** Карта команд: Имя -> Объект команды */
    private final Map<String, Command> commands = new LinkedHashMap<>();
    /** История последних 14 команд */
    private final List<String> history = new LinkedList<>();

    /**
     * Конструктор серверного менеджера команд.
     * Заметь: здесь больше нет asker и reader, так как ввод на клиенте.
     */
    public CommandManager(CollectionManager collection, FileManager file) {
        // Регистрация команд (Классы команд тоже нужно будет обновить под Response)
        commands.put("help", new HelpCommand(commands));
        commands.put("info", new InfoCommand(collection));
        commands.put("show", new ShowCommand(collection));
        commands.put("insert", new InsertCommand(collection));
        commands.put("update", new UpdateCommand(collection));
        commands.put("remove_key", new RemovekeyCommand(collection));
        commands.put("clear", new ClearCommand(collection));
        // Save доступна только серверу (можно вызвать вручную в коде сервера)

        //depracated
        commands.put("save", new SaveCommand(collection, file));
        commands.put("execute_script", new ExecuteScriptCommand(this));

        commands.put("remove_lower", new RemoveLowerCommand(collection));
        commands.put("history", new HistoryCommand(history));
        commands.put("replace_if_greater", new ReplaceIfGreaterCommand(collection));

        //лямбда
        commands.put("group_counting_by_minimal_point", new GroupCountingByMinimalCommand(collection));
        commands.put("count_less_difficulty", new CountLessThanDifficulty(collection));
        commands.put("print_field_descending_minimal_point", new PrintFieldDescendingMinimalPointCommand(collection));

        // Рофло-команды
        commands.put("gavrilovsay", new GavrilovsayCommand());
        commands.put("polyakov", new PolyakovCommand());

        logger.info("Зарегистрировано {} команд", commands.size());
    }

    /**
     * Основной метод обработки сетевого запроса.
     * @param request объект запроса от клиента.
     * @return объект ответа для отправки по сети.
     */
    public Response handle(Request request) {
        String commandName = request.getCommandName().toLowerCase();
        Command command = commands.get(commandName);

        if (command != null) {
            addToHistory(commandName);
            logger.info("Выполнение команды: {}", commandName);
            // Каждая команда на сервере теперь возвращает Response
            return command.execute(request);
        } else {
            logger.warn("Команда {} не найдена", commandName);
            return new Response("Ошибка: Команда '" + commandName + "' не найдена. Введите 'help'.", false);
        }
    }
    /**
     * Добавляет команду в список последних использованных команд
     * @param cmd Строковое название команды
     */
    private void addToHistory(String cmd) {
        history.add(cmd);
        if (history.size() > 14) history.remove(0);
    }
}