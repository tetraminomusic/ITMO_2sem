package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.LabWork;
import network.Request;
import network.Response;

public class UpdateCommand implements Command{
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public UpdateCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    @Override
    public Response execute(Request request) {
        int id = Integer.parseInt(request.getArgument());
        LabWork newData = request.getObjectArgument();
        String login = request.getLogin();

        //попытка обновления
        if (databaseManager.updateLabWork(id, newData, login)) {
            newData.setID(id);
            newData.setOwnerLogin(login);
            collectionManager.getCollection().put(String.valueOf(id), newData);
            return new Response("Объект [ID:" + id + "] обновлен.", true);
        }
        return new Response("Ошибка: Элемент не найден или вы не его владелец.", false);
    }

    @Override
    public String getDescription() {
        return "update id {element} : обновить значение элемента коллекции по ID";
    }
}