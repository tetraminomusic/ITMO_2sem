package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.LabWork;
import network.Request;
import network.Response;

import java.util.List;
import java.util.stream.Collectors;

public class RemoveLowerCommand implements Command{
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

       //подбираем id тех, кто подходит под условие (ну и автор был обращающийся)
        List<Integer> idsToDelete = collectionManager.getCollection().values().stream()
                .filter(lab -> lab.getOwnerLogin().equals(login))
                .filter(lab -> lab.compareTo(ref) < 0)
                .map(LabWork::getId)
                .collect(Collectors.toList());

        //удаляем их по одному в БД
        int count = 0;
        for (int id: idsToDelete) {
            if (databaseManager.deleteLabWork(id, login)) {
                collectionManager.getCollection().remove(String.valueOf(id));
                count++;
            }
        }

        return new Response("Удалено ваших элементов: " + count, true);
    }


    @Override
    public String getDescription() {
        return "Удаляет из коллекции все элементы, меньше заданного";
    }
}
