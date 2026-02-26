package commands;

import managers.CollectionManager;
import models.LabWork;
import network.Request;
import network.Response;

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
     * @param request - аргумент команды. Не используется в данной команде.
     */
    @Override
    public Response execute(Request request) {
        //collect - терминальная операция, которая собирает элементы потока в Map
        Map<Float, Long> groups = collectionManager.getCollection().values().stream()
                .collect(Collectors.groupingBy(labWork -> labWork.getMinimalPoint(), Collectors.counting()));

        if (groups.isEmpty()) {
            return new Response("Коллекция пуста", true);
        } else {
            //превращаем каждую запись в нужный нам вид и склеиваем их по \n
            String result = groups.entrySet().stream()
                    .map(entry -> "Минимальный балл [" + entry.getKey() + "]: " + entry.getValue() + " шт.")
                    .collect(Collectors.joining("\n"));
            return new Response(result, true);
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
