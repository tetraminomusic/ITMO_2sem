package commands;

import managers.CollectionManager;
import models.LabWork;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Команда, которая группирует элементы коллекции по минимальным баллам и выводит их количество
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class GroupCountingByMinimalCommand  implements Command {
    /**
     * Менеджер коллекции, из которой извлекают саму коллекцию элементов
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции, из которой извлекают саму коллекцию элементов
     */
    public GroupCountingByMinimalCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды
     * @param arg - аргумент команды. Не используется в данной команде.
     */
    @Override
    public void execute(String arg) {
        //collect - терминальная операция, которая собирает элементы потока в Map
        Map<Float, Long> groups = collectionManager.getCollection().values().stream().collect(Collectors.groupingBy(labWork -> labWork.getMinimalPoint(), Collectors.counting()));

        if (groups.isEmpty()) {
            System.out.println("Коллекция пуста");
        } else {
            groups.forEach((point,count) -> System.out.println("Минимальный балл [" + point + "]: " + count + " шт."));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Группирует элементы коллекции по минимальным баллам и выводит их количество";
    }
}
