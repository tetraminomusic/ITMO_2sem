package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.LabWork;
import network.Request;
import network.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 * Команда для обновления элемента.
 * Возвращает ключи локализации вместо текста.
 */
public class UpdateCommand implements Command {
    private static final Logger logger = LoggerFactory.getLogger(UpdateCommand.class);
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public UpdateCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    @Override
    public Response execute(Request request) {
        String arg = request.getArgument();
        String login = request.getLogin();

        if (arg == null || arg.isEmpty()) {
            return new Response("server.msg.error_no_id", false, null);
        }

        try {
            int id = Integer.parseInt(arg);
            Map.Entry<String, LabWork> entryToUpdate = collectionManager.getCollection().entrySet().stream()
                    .filter(entry -> entry.getValue().getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (entryToUpdate == null) {
                return new Response("server.msg.not_found", false, null);
            }

            LabWork oldLab = entryToUpdate.getValue();

            if (!oldLab.getOwnerLogin().equals(login)) {
                return new Response("server.msg.access_denied", false, null);
            }

            LabWork updatedLab = request.getObjectArgument();
            if (updatedLab == null) {
                return new Response("server.msg.id_confirmed", true, null);
            }

            if (databaseManager.updateLabWork(id, updatedLab, login)) {
                updatedLab.setID(id);
                updatedLab.setOwnerLogin(login);
                updatedLab.setCreationDate(oldLab.getCreationDate());
                collectionManager.getCollection().put(entryToUpdate.getKey(), updatedLab);

                logger.info("Пользователь {} обновил объект ID {}", login, id);

                return new Response("server.msg.update_success", true, null, id);
            } else {
                return new Response("server.msg.error_db", false, null);
            }

        } catch (NumberFormatException e) {
            return new Response("server.msg.error_invalid_id", false, null);
        }
    }

    @Override
    public String getDescription() {
        return "update id {element} : обновить значение элемента коллекции по ID";
    }
}