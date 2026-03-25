package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.LabWork;
import network.Request;
import network.Response;

public class ReplaceIfGreaterCommand implements Command{
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public ReplaceIfGreaterCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    @Override
    public Response execute(Request request) {
        String key = request.getArgument();
        String login = request.getLogin();
        LabWork newElement = request.getObjectArgument();

        //Базовые проверки
        if (key == null || key.isEmpty()) {
            return new Response("Ошибка: Введите ключ (ID) после команды!", false, null);
        }

        if (newElement == null) {
            return new Response("Ошибка: Данные объекта не получены", false, null);
        }

        //Ищем старый элемент в памяти
        LabWork oldElement = collectionManager.getCollection().get(key);

        if (oldElement == null) {
            return new Response("Ошибка: Элемент с ключом " + key + " не найден!", false, null);
        }

        if (!oldElement.getOwnerLogin().equals(login)) {
            return new Response("Ошибка: Вы не являетесь владельцем этого объекта. Замена запрещена.", false, null);
        }

        //СРАВНЕНИЕ (compareTo)
        if (newElement.compareTo(oldElement) > 0) {

            //Пытаемся обновить в БАЗЕ ДАННЫХ
            //Поскольку мы заменяем "тело" объекта, сохраняя "слот", используем updateLabWork
            int id = oldElement.getId();
            if (databaseManager.updateLabWork(id, newElement, login)) {

                //СИНХРОНИЗИРУЕМ ПАМЯТЬ
                newElement.setID(id);
                newElement.setOwnerLogin(login);
                newElement.setCreationDate(oldElement.getCreationDate()); // Сохраняем оригинальную дату

                collectionManager.getCollection().put(key, newElement);

                return new Response("Успех: Новый элемент больше старого. Замена произведена в БД и памяти.", true, null);
            } else {
                return new Response("Ошибка: Не удалось обновить запись в базе данных.", false, null);
            }
        } else {
            return new Response("Замена не произведена: Новый элемент меньше или равен старому.", true, null);
        }
    }


    @Override
    public String getDescription() {
        return "Заменяет значение по ключу, если новое значение больше старого";
    }

}
