package commands;
import managers.CollectionManager;
import managers.DatabaseManager;
import network.Request;
import network.Response;


public class ClearCommand implements Command{
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public ClearCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.collectionManager = collectionManager;
    }

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

    @Override
    public String getDescription() {
        return "Очищает коллекцию";
    }
}
