package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import network.Request;
import network.Response;


public class RemovekeyCommand implements Command {
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public RemovekeyCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        String key = request.getArgument();
        String login = request.getLogin();

        if (databaseManager.deleteLabWork(Integer.parseInt(key), login)) {
            //как только удалила БД, удаляем у себя в памяти
            collectionManager.getCollection().remove(key);
            return new Response("Объект успешно удалён", true, null);
        } else {
            return new Response("Ошибка: объект не найден или он вам не принадлежит", false, null);
        }
    }

    @Override
    public String getDescription() {
        return "Удаляет элемент из коллекции по его ключу";
    }
}
