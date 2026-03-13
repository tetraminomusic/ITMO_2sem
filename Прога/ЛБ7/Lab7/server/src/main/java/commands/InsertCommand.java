package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.LabWork;
import network.Request;
import network.Response;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Команда, которая добавляет новый элемент в коллекцию с заданным ключом.
 * @update Адаптирован под БД
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class InsertCommand implements Command{
    private static final Logger logger = LoggerFactory.getLogger(InsertCommand.class);
    /**
     * Менеджер Коллекции
     */
    private final CollectionManager collectionManager;

    /**
     * Менеджер БД
     */
    private final DatabaseManager databaseManager;
    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекции для выполнения операций
     */
    public InsertCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    /**
     * Выполнение логики команды
     * @new Адаптирован под БД
     * @param request - аргумент команды, который является ключом для нового элемента коллекции.
     */
    @Override
    public Response execute(Request request) {
        LabWork lab = request.getObjectArgument();
        String login = request.getLogin();

        if (lab == null) return new Response("Ошибка: объект не передан", false);

        //пытаемся сохранить в БД
        int newId = databaseManager.insertLabWork(lab, login);

        if (newId != -1) {
            //если объект сохранился в БД
            lab.setID(newId);
            lab.setOwnerLogin(login);

            //синхронизируем память
            collectionManager.getCollection().put(String.valueOf(newId), lab);

            return new Response("Объект успешно добавлен (ID: " + newId + ")", true);
        } else {
            return new Response("Ошибка при записи в БД", false);
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