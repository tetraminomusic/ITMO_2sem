package commands;

import managers.CollectionManager;
import network.Request;
import network.Response;

import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

/**
 * Команда, которая выводит в консоль все элементы коллекции.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class ShowCommand implements Command{
    /**
     * Менеджер коллекции, из которого извлекается сама коллекция.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекции, из которого извлекается сама коллекция
     */
    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды.
     * @param request аргумент команды. Не используется в данной команде.
     */
    @Override
    public Response execute(Request request) {
        var collection = collectionManager.getCollection();

        if (collection.isEmpty()) {
            return new Response("Коллекция пуста.", true);
        }
        //когда мы вызываем value, мы вызываем метод to String, который мы переопределили
        //-> лямбда выражение
        String result = collection.entrySet().stream()
                .sorted(comparingByValue())
                .map(entry -> "Ключ: " + entry.getKey() + " | " + entry.getValue().toString())
                .collect(Collectors.joining("\n"));
        return new Response(result, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Выводит все элементы коллекции в строковом представлении";
    }
}
