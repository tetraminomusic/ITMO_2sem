package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import network.Request;
import network.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Команда для очистки коллекции (только своих объектов).
 * Возвращает ключ локализации и количество удаленных элементов.
 */
public class ClearCommand implements Command {
    private static final Logger logger = LoggerFactory.getLogger(ClearCommand.class);
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public ClearCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    @Override
    public Response execute(Request request) {
        String login = request.getLogin();

        long countBefore = collectionManager.getCollection().values().stream()
                .filter(lab -> lab.getOwnerLogin().equals(login))
                .count();

        if (databaseManager.clearUserObjects(login)) {

            collectionManager.getCollection().entrySet().removeIf(entry ->
                    entry.getValue().getOwnerLogin().equals(login)
            );
            logger.info("Пользователь {} очистил свою коллекцию ({} шт.)", login, countBefore);

            return new Response("server.msg.clear_success", true, null, countBefore);

        } else {
            logger.error("Ошибка при попытке очистки коллекции пользователем {}", login);
            return new Response("server.msg.error_db", false, null);
        }
    }

    @Override
    public String getDescription() {
        return "очистить коллекцию (только свои объекты)";
    }
}