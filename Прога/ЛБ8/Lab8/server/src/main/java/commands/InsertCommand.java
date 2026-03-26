package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.LabWork;
import network.Request;
import network.Response;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class InsertCommand implements Command {
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

        if (lab == null) {
            return new Response("server.msg.error_no_object", false, null);
        }

        int newId = databaseManager.insertLabWork(lab, login);

        if (newId != -1) {
            lab.setID(newId);
            lab.setOwnerLogin(login);

            collectionManager.getCollection().put(String.valueOf(newId), lab);

            logger.info("Пользователь {} добавил объект с ID {}", login, newId);
            return new Response("server.msg.insert_success", true, null, newId);

        } else {
            logger.error("Ошибка записи в БД для пользователя {}", login);
            return new Response("server.msg.error_db", false, null);
        }
    }

    @Override
    public String getDescription() {
        return "insert null {element} : добавить новый элемент с заданным ключом";
    }
}