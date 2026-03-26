package commands;

import managers.CollectionManager;
import models.LabWork;
import network.Request;
import network.Response;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда 'show'.
 * Возвращает актуальный список объектов коллекции, отсортированный по местоположению.
 */
public class ShowCommand implements Command {
    private final CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        var collection = collectionManager.getCollection();

        if (collection.isEmpty()) {
            return new Response("server.msg.empty", true, new ArrayList<>());
        }

        List<LabWork> sortedList = collection.values().stream()
                .sorted(Comparator.comparing(LabWork::getCoordinates))
                .collect(Collectors.toList());

        return new Response("server.msg.success", true, sortedList);
    }

    @Override
    public String getDescription() {
        return "вывести все элементы коллекции";
    }
}