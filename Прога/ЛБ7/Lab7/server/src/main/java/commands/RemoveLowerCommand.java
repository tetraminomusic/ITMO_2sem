package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.LabWork;
import network.Request;
import network.Response;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда, которая удаляет все элементы из коллекции, меньше заданного.
 * @UPDATE адаптирован под БД
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class RemoveLowerCommand implements Command{
    /**
     * Менеджер коллекции, из которой извлекают саму коллекцию элементов.
     */
    private final CollectionManager collectionManager;

    /**
     * Менеджер БД
     */
    private final DatabaseManager databaseManager;
    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции, из которой извлекают саму коллекцию элементов
     */
    public RemoveLowerCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды.
     * @UPDATE адаптирован под БД
     * @param request аргумент команды. Не используется в данном методе.
     */
    @Override
    public Response execute(Request request) {
       LabWork ref = request.getObjectArgument();
       String login = request.getLogin();

       //подбираем id тех, кто подходит под условие (ну и автор был обращающийся)
        List<Integer> idsToDelete = collectionManager.getCollection().values().stream()
                .filter(lab -> lab.getOwnerLogin().equals(login))
                .filter(lab -> lab.compareTo(ref) < 0)
                .map(LabWork::getId)
                .collect(Collectors.toList());

        //удаляем их по одному в БД
        int count = 0;
        for (int id: idsToDelete) {
            if (databaseManager.deleteLabWork(id, login)) {
                collectionManager.getCollection().remove(String.valueOf(id));
                count++;
            }
        }

        return new Response("Удалено ваших элементов: " + count, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Удаляет из коллекции все элементы, меньше заданного";
    }
}
