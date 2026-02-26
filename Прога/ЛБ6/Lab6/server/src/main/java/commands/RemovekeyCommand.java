package commands;

import managers.CollectionManager;
import network.Request;
import network.Response;

/**
 * Команда, которая удаляет элемент коллекции по его ключу.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class RemovekeyCommand implements Command {
    /**
     * Менеджер коллекции, из которого извлекается сама коллекция.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекции, из которой извлекается сама коллекция
     */
    public RemovekeyCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды.
     * @param request аргумент команды, являющийся ID элемента, который мы хотим удалить из коллекции.
     */
    @Override
    public Response execute(Request request) {
        String arg = request.getArgument();
        if (arg == null || arg.isEmpty()) {
            return new Response("\u001B[31mОшибка\u001B[0m: введите ключ после названия команды!", false);
        }
        if (collectionManager.getCollection().containsKey(arg)) {
            collectionManager.getCollection().remove(arg);
            return new Response("Элемент с ключом " + arg + " был успешно удалён", true);
        } else {
            return new Response("\u001B[31mОшибка\u001B[0m: элемент с ключом " + arg + " не был найден!", false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Удаляет элемент из коллекции по его ключу";
    }
}
