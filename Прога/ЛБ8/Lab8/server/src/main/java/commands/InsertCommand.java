package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.LabWork;
import network.Request;
import network.Response;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class InsertCommand implements Command{
    private static final Logger logger = LoggerFactory.getLogger(InsertCommand.class);
    private final CollectionManager collectionManager;

    private final DatabaseManager databaseManager;
    public InsertCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    @Override
    public Response execute(Request request) {
        LabWork lab = request.getObjectArgument();
        String login = request.getLogin();

        if (lab == null) return new Response("Ошибка: объект не передан", false, null);

        //пытаемся сохранить в БД
        int newId = databaseManager.insertLabWork(lab, login);

        if (newId != -1) {
            //если объект сохранился в БД
            lab.setID(newId);
            lab.setOwnerLogin(login);

            //синхронизируем память
            collectionManager.getCollection().put(String.valueOf(newId), lab);

            return new Response("Объект успешно добавлен (ID: " + newId + ")", true, null);
        } else {
            return new Response("Ошибка при записи в БД", false, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "insert null {element} : добавить новый элемент с заданным ключом";
    }
}