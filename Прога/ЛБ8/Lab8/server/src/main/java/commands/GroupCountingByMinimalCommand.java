package commands;

import managers.CollectionManager;
import models.LabWork;
import network.Request;
import network.Response;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Команда для группировки элементов по минимальному баллу.
 */
public class GroupCountingByMinimalCommand implements Command {
    private final CollectionManager collectionManager;

    public GroupCountingByMinimalCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        Map<Float, Long> groups = collectionManager.getCollection().values().stream()
                .collect(Collectors.groupingBy(LabWork::getMinimalPoint, Collectors.counting()));

        if (groups.isEmpty()) {
            return new Response("server.msg.empty", true, null);
        }

        String result = groups.entrySet().stream()
                .map(entry -> entry.getKey() + " -> " + entry.getValue())
                .collect(Collectors.joining("\n"));
        return new Response("server.msg.group_success", true, null, result);
    }

    @Override
    public String getDescription() {
        return "сгруппировать элементы коллекции по минимальному баллу";
    }
}