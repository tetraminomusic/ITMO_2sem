package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.LabWork;
import network.Request;
import network.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда для удаления объектов, которые меньше заданного эталона.
 * Удаляет только объекты, принадлежащие текущему пользователю.
 */
public class RemoveLowerCommand implements Command {
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public RemoveLowerCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        LabWork ref = request.getObjectArgument();
        String login = request.getLogin();

        if (ref == null) {
            return new Response("server.msg.error_no_object", false, null);
        }

        List<Integer> idsToDelete = collectionManager.getCollection().values().stream()
                .filter(lab -> lab.getOwnerLogin().equals(login))
                .filter(lab -> lab.compareTo(ref) < 0)
                .map(LabWork::getId)
                .collect(Collectors.toList());

        int count = 0;
        for (int id : idsToDelete) {
            if (databaseManager.deleteLabWork(id, login)) {
                collectionManager.getCollection().remove(String.valueOf(id));
                count++;
            }
        }

        return new Response("server.msg.remove_lower_success", true, null, count);
    }

    @Override
    public String getDescription() {
        return "удалить из коллекции все элементы, меньшие, чем заданный (только свои)";
    }
}