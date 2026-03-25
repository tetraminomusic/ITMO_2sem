package commands;

import managers.CollectionManager;
import network.Request;
import network.Response;

import java.util.Map;
import java.util.stream.Collectors;


public class GroupCountingByMinimalCommand  implements Command {
    private final CollectionManager collectionManager;

    public GroupCountingByMinimalCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        Map<Float, Long> groups = collectionManager.getCollection().values().stream()
                .collect(Collectors.groupingBy(labWork -> labWork.getMinimalPoint(), Collectors.counting()));

        if (groups.isEmpty()) {
            return new Response("Коллекция пуста", true, null);
        } else {
            String result = groups.entrySet().stream()
                    .map(entry -> "Минимальный балл [" + entry.getKey() + "]: " + entry.getValue() + " шт.")
                    .collect(Collectors.joining("\n"));
            return new Response(result, true, null);
        }
    }

    @Override
    public String getDescription() {
        return "Группирует элементы коллекции по минимальным баллам и выводит их количество";
    }
}
