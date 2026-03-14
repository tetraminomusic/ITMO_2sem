package commands;

import managers.CollectionManager;
import network.Request;
import network.Response;

import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;


public class ShowCommand implements Command{
    private final CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }


    @Override
    public Response execute(Request request) {
        var collection = collectionManager.getCollection();

        if (collection.isEmpty()) {
            return new Response("Коллекция пуста.", true);
        }
        String result = collection.entrySet().stream()
                .sorted(comparingByValue())
                .map(entry -> "Ключ: " + entry.getKey() + " | " + entry.getValue().toString())
                .collect(Collectors.joining("\n"));
        return new Response(result, true);
    }

    @Override
    public String getDescription() {
        return "Выводит все элементы коллекции в строковом представлении";
    }
}
