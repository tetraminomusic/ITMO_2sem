package commands;

import managers.CollectionManager;
import models.LabWork;
import network.Request;
import network.Response;
import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

/**
 * Команда, которая добавляет новый элемент в коллекцию с заданным ключом.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class InsertCommand implements Command{
    private static final Logger logger = (Logger) LoggerFactory.getLogger(InsertCommand.class);
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
        String key = request.getArgument();
        LabWork newLab = request.getObjectArgument();

        if (key == null || key.isEmpty()) {
            return new Response("\u001B[31mОшибка\u001B[0m: Не указан ключ для вставки!", false);
        }

        if (newLab == null) {
            return new Response("\u001B[31mОшибка\u001B[0m: на сервер не переданы данные объекта!", false);
        }

        //существует ли уже элемент с таким ключом через менеджер
        if (collectionManager.containsKey(key)) {
            return new Response("\u001B[31mОшибка\u001B[0m: Элемент с ключом \"" + key + "\" уже существует!", false);
        }

        try {
            newLab.setID(collectionManager.generateNextId());

            collectionManager.getCollection().put(key, newLab);
        } catch (Exception e) {
            logger.info("Ошибка при вставке элемента: " + e.getMessage());
            return new Response("Критическая ошибка на сервере при добавлении нового объекта", false);
        }


        //создаём новый объект LabWork через Asker


        //ОБНОВА

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "insert null {element} : добавить новый элемент с заданным ключом";
    }
}