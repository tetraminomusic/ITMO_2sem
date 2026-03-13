package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import network.Request;
import network.Response;

/**
 * Команда, которая удаляет элемент коллекции по его ключу.
 *
 * @author Малых Кирилл Романович
 * @update Адаптация для работы с БД
 * @version 1.0
 */
public class RemovekeyCommand implements Command {
    /**
     * Менеджер коллекции, из которого извлекается сама коллекция.
     */
    private final CollectionManager collectionManager;

    /**
     * Менеджер управления БД
     */
    private final DatabaseManager databaseManager;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекции, из которой извлекается сама коллекция
     */
    public RemovekeyCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды.
     * @new Теперь обращается к БД
     * @param request аргумент команды, являющийся ID элемента, который мы хотим удалить из коллекции.
     */
    @Override
    public Response execute(Request request) {
        String key = request.getArgument();
        String login = request.getLogin();

        if (databaseManager.deleteLabWork(Integer.parseInt(key), login)) {
            //как только удалила БД, удаляем у себя в памяти
            collectionManager.getCollection().remove(key);
            return new Response("Объект успешно удалён", true);
        } else {
            return new Response("Ошибка: объект не найден или он вам не принадлежит", false);
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
