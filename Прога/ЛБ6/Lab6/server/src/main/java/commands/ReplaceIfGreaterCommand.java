package commands;

import managers.CollectionManager;
import models.LabWork;
import network.Request;
import network.Response;

/**
 * Команда, которая заменяет значение по ключу, если новое значение больше старого.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class ReplaceIfGreaterCommand implements Command{
    /**
     * Менеджер коллекции, из которой извлекают саму коллекцию элементов.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции, из которой извлекают саму коллекцию элементов.
     */
    public ReplaceIfGreaterCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды.
     * @param request аргумент команды, который является ключом элемента в коллекции.
     */
    @Override
    public Response execute(Request request) {
        String arg = request.getArgument();
        if (arg == null || arg.isEmpty()) {
            return new Response("\u001B[31mОшибка\u001B[0m: Введите ключ после названия команды!", false);
        }

        if (!collectionManager.getCollection().containsKey(arg)) {
            return new Response("\u001B[31mОшибка\u001B[0m: Элемент с ключом " + arg + " не найден!", false);
        }

        LabWork newElement = request.getObjectArgument();
        if (newElement == null) {
            return new Response("\u001B[31mОшибка\u001B[0m: Клиент не прислал элемент для замены", false);
        }
        LabWork oldElement = collectionManager.getCollection().get(arg);

        // Сравнение относительно имен (поменять можно в классе LabWork)
        if (newElement.compareTo(oldElement) > 0) {
            newElement.setID(collectionManager.generateNextId());
            collectionManager.getCollection().put(arg, newElement);
            return new Response("Новый элемент больше старого - Замена произведена!", true);
        } else {
            return new Response("Новый элемент меньше старого - Замена не произведена!", true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Заменяет значение по ключу, если новое значение больше старого";
    }

}
