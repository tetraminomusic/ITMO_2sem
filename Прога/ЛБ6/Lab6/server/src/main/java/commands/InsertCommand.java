package commands;

import managers.CollectionManager;
import models.LabWork;
import network.Request;
import network.Response;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Команда, которая добавляет новый элемент в коллекцию с заданным ключом.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class InsertCommand implements Command{
    private static final Logger logger = LoggerFactory.getLogger(InsertCommand.class);
    private final CollectionManager collectionManager;
    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекции для выполнения операций
     */
    public InsertCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды
     * @param request - аргумент команды, который является ключом для нового элемента коллекции.
     */
    @Override
    public Response execute(Request request) {
        LabWork newLab = request.getObjectArgument();

        if (newLab == null) {
            return new Response("\u001B[31mОшибка\u001B[0m: на сервер не переданы данные объекта!", false);
        }

        Integer newId = collectionManager.generateNextId();
        newLab.setID(newId);

        String generatedKey = String.valueOf(newId);

        try {
            collectionManager.getCollection().put(generatedKey, newLab);
            logger.info("В коллекцию добавлен новый элемент с ключом: {}", generatedKey);
            return new Response("Объект успешно добавлен под ключом '" + generatedKey + "'.", true);
        } catch (Exception e) {
            logger.info("Ошибка при вставке элемента: {}", e.getMessage());
            return new Response("Критическая ошибка на сервере при добавлении нового объекта", false);
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