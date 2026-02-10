package commands;

import managers.CollectionManager;
import models.LabWork;

import java.util.Map;
import java.util.stream.Collectors;

public class GroupCountingByMinimalCommand  implements Command {
    private final CollectionManager collectionManager;

    public GroupCountingByMinimalCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String arg) {
        Map<Float, Long> groups = collectionManager.getCollection().values().stream().collect(Collectors.groupingBy(LabWork::getMinimalPoint, Collectors.counting()));

        if (groups.isEmpty()) {
            System.out.println("Коллекция пуста");
        } else {
            groups.forEach((point,count) -> System.out.println("Минимальный балл [" + point + "]: " + count + " шт."));
        }
    }

    @Override
    public String getDescription() {
        return "Группирует элементы коллекции по минимальным баллам и выводит их количество";
    }
}
