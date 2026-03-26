package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.LabWork;
import network.Request;
import network.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Команда для удаления объекта по ключу.
 * Проверяет права владения перед удалением.
 */
public class RemovekeyCommand implements Command {
    private static final Logger logger = LoggerFactory.getLogger(RemovekeyCommand.class);
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public RemovekeyCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    @Override
    public Response execute(Request request) {
        String key = request.getArgument(); // В нашей системе ключ совпадает с ID
        String login = request.getLogin();

        if (key == null || key.isEmpty()) {
            return new Response("server.msg.error_no_id", false, null);
        }

        try {
            int id = Integer.parseInt(key);

            LabWork labToRemove = collectionManager.getCollection().get(key);

            if (labToRemove == null) {
                return new Response("server.msg.not_found", false, null);
            }

            if (!labToRemove.getOwnerLogin().equals(login)) {
                logger.warn("Пользователь {} пытался удалить чужой объект ID {}", login, id);
                return new Response("server.msg.access_denied", false, null);
            }

            if (databaseManager.deleteLabWork(id, login)) {
                collectionManager.getCollection().remove(key);

                logger.info("Пользователь {} удалил свой объект ID {}", login, id);
                return new Response("server.msg.delete_success", true, null);

            } else {
                return new Response("server.msg.error_db", false, null);
            }

        } catch (NumberFormatException e) {
            return new Response("server.msg.error_invalid_id", false, null);
        }
    }

    @Override
    public String getDescription() {
        return "удалить элемент из коллекции по его ключу (только свои)";
    }
}