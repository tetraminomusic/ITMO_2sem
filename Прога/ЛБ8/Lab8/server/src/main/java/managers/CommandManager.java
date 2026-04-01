package managers;

import commands.*;
import commands.memes.GavrilovsayCommand;
import commands.memes.PolyakovCommand;
import models.LabWork;
import network.Request;
import network.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Менеджер команд для серверной части.
 * Принимает объект Request, находит нужную команду и возвращает Response.
 * Также отвечает за команду History
 */
public class CommandManager {
    /**
     * Логгер для сетевого менеджера
     */
    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

    /** Карта команд: Имя -> Объект команды */
    private final Map<String, Command> commands = new LinkedHashMap<>();
    /** История последних 14 команд */
    private final List<String> history = new CopyOnWriteArrayList<>();
    /**
     * Менеджер для работы с БД
     */
    private final DatabaseManager databaseManager;

    private final CollectionManager collectionManager;
    /**
     * Конструктор серверного менеджера команд.
     * Заметь: здесь больше нет asker и reader, так как ввод на клиенте.
     */
    public CommandManager(CollectionManager collection, DatabaseManager databaseManager) {
        this.collectionManager = collection;
        this.databaseManager = databaseManager;

        // Регистрация команд (Классы команд тоже нужно будет обновить под Response)
        commands.put("help", new HelpCommand(commands));
        commands.put("info", new InfoCommand(collection));
        commands.put("show", new ShowCommand(collection));
        commands.put("insert", new InsertCommand(collection, databaseManager));
        commands.put("update", new UpdateCommand(collection, databaseManager));
        commands.put("remove_key", new RemovekeyCommand(collection, databaseManager));
        commands.put("clear", new ClearCommand(collection, databaseManager));
        commands.put("auth", new AuthCommand());
        // Save доступна только серверу (можно вызвать вручную в коде сервера)

        //depracated
        commands.put("save", new SaveCommand(collection));
        commands.put("execute_script", new ExecuteScriptCommand());

        commands.put("remove_lower", new RemoveLowerCommand(collection, databaseManager));
        commands.put("history", new HistoryCommand(history));
        commands.put("replace_if_greater", new ReplaceIfGreaterCommand(collection, databaseManager));

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
        String login = request.getLogin();
        String password = request.getPassword();
        String commandName = request.getCommandName().toLowerCase();

        // 1. Проверка авторизации
        if (!databaseManager.authenticate(login, password)) {
            logger.warn("Отказ в доступе для пользователя: {}", login);
            return new Response("server.msg.auth_failed", false, null);
        }

        Command command = commands.get(commandName);

        if (command != null) {
            addToHistory(commandName);
            try {
                // 2. Выполнение команды
                Response commandResponse = command.execute(request);

                // 3. СИНХРОНИЗАЦИЯ (ТЗ: отсортированы по местоположению)
                // Мы берем текущую коллекцию из памяти и превращаем её в список
                List<LabWork> currentCollection = collectionManager.getCollection().values().stream()
                        // Сортировка по координатам (используем Comparable в Coordinates)
                        .sorted(Comparator.comparing(LabWork::getCoordinates))
                        .collect(Collectors.toList());

                // Возвращаем ответ команды, дополненный свежим списком объектов
                return new Response(
                        commandResponse.getMessage(),
                        commandResponse.getSuccess(),
                        currentCollection,
                        commandResponse.getMessageArgs()
                );

            } catch (Exception e) {
                logger.error("Ошибка при выполнении команды {}: {}", commandName, e.getMessage());
                return new Response("Критическая ошибка на сервере при выполнении команды.", false, null);
            }
        } else {
            return new Response("Команда '" + commandName + "' не найдена.", false, null);
        }
    }
    /**
     * Добавляет команду в список последних использованных команд
     * @param cmd Строковое название команды
     */
    private synchronized void addToHistory(String cmd) {
        history.add(cmd);
        if (history.size() > 14) history.remove(0);
    }
}