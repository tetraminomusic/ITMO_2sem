package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.LabWork;
import network.Request;
import network.Response;

public class ReplaceIfGreaterCommand implements Command {
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public ReplaceIfGreaterCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    @Override
    public Response execute(Request request) {
        String key = request.getArgument();
        String login = request.getLogin();

        // 1. Ищем старый элемент
        LabWork oldElement = collectionManager.getCollection().get(key);
        if (oldElement == null) {
            return new Response("server.msg.not_found", false, null);
        }

        // 2. ПРОВЕРКА ПРАВ (Самое важное!)
        if (!oldElement.getOwnerLogin().equals(login)) {
            return new Response("server.msg.access_denied", false, null);
        }

        // 3. ПРОВЕРКА: "Dry Run" или реальная замена?
        LabWork newElement = request.getObjectArgument();
        if (newElement == null) {
            // Клиент просто проверяет, можно ли ему трогать этот ID
            return new Response("server.msg.id_confirmed", true, null);
        }

        // 4. Реальная логика сравнения и замены
        if (newElement.compareTo(oldElement) > 1) {
            int id = oldElement.getId();
            if (databaseManager.updateLabWork(id, newElement, login)) {

                // Синхронизация памяти
                newElement.setID(id);
                newElement.setOwnerLogin(login);
                newElement.setCreationDate(oldElement.getCreationDate());

                collectionManager.getCollection().put(key, newElement);

                return new Response("server.msg.replace_greater_success", true, null);
            } else {
                return new Response("server.msg.error_db", false, null);
            }
        } else {
            return new Response("server.msg.replace_greater_fail", true, null);
        }
    }

    @Override
    public String getDescription() {
        return "replace_if_greater null {element} : замена, если новое значение больше";
    }
}