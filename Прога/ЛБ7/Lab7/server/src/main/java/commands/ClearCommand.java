package commands;
import managers.CollectionManager;
import managers.DatabaseManager;
import network.Request;
import network.Response;

/**
 * Команда очистки коллекции, то есть удаление всех ещё элементов
 * @new адаптирован по БД
 * @author Малых Кирилл Романович
 * @version 1.0
 */

public class ClearCommand implements Command{
    /** Менеджер коллекции, к которому обращается команда для выполнения очистки. */
    private final CollectionManager collectionManager;

    /**
     * Менеджер БД
     */
    private final DatabaseManager databaseManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции, которая будет очищена
     */
    public ClearCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды.
     * @param request не используется в данной команде
     */
    @Override
    public Response execute(Request request) {
        String login = request.getLogin();

        //удаляем из БД записи этого пользователя
        if (databaseManager.clearUserObjects(login)) {
            //удаляем из памяти только те объекты, которые принадлежат пользователю
            collectionManager.getCollection().entrySet().removeIf(entry ->
                    entry.getValue().getOwnerLogin().equals(login)
            );

            return new Response("Ваши объекты были успешно удалены", true);
        }
        return new Response("Ошибка при очистке БД", false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Очищает коллекцию";
    }
}
