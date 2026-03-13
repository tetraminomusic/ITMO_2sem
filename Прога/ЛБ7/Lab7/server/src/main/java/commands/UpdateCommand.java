package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.LabWork;
import network.Request;
import network.Response;

/**
 * Команда, которая обновляет значение элемента по ключу.
 * @UPDATE адаптирован под БД
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class UpdateCommand implements Command{
    /**
     * Менеджер коллекции, который управляет CRUD операциями.
     */
    private final CollectionManager collectionManager;

    /**
     * Менеджер БД
     */
    private final DatabaseManager databaseManager;


    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекции для выполнения операций
     */
    public UpdateCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    /**
     * Выполнение логики команды.
     * @new адаптирован под БД
     * @param request аргумент команды, являющийся ID элемента, значение которого мы хотим обновить.
     */
    @Override
    public Response execute(Request request) {
        int id = Integer.parseInt(request.getArgument());
        LabWork newData = request.getObjectArgument();
        String login = request.getLogin();

        //попытка обновления
        if (databaseManager.updateLabWork(id, newData, login)) {
            newData.setID(id);
            newData.setOwnerLogin(login);
            collectionManager.getCollection().put(String.valueOf(id), newData);
            return new Response("Объект [ID:" + id + "] обновлен.", true);
        }
        return new Response("Ошибка: Элемент не найден или вы не его владелец.", false);
    }




    /**
     * Вспомогательный метод для поиска ключа по ID.
     * @param id идентификатор элемента
     * @return ключ элемента или null, если не найден
     * @deprecated всё переместил в основной метод
     */
    private String findKeyById(Integer id) {
        if (id == null || collectionManager.isEmpty()) {
            return null;
        }

        // Проходим по всем записям коллекции
        for (var entry : collectionManager.getCollection().entrySet()) {
            if (entry.getValue().getId().equals(id)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "update id {element} : обновить значение элемента коллекции по ID";
    }
}