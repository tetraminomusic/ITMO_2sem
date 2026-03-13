package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.LabWork;
import network.Request;
import network.Response;

/**
 * Команда, которая заменяет значение по ключу, если новое значение больше старого.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class ReplaceIfGreaterCommand implements Command{
    /**
     * Менеджер коллекции, из которой извлекают саму коллекцию элементов.
     */
    private final CollectionManager collectionManager;

    private final DatabaseManager databaseManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции, из которой извлекают саму коллекцию элементов.
     */
    public ReplaceIfGreaterCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    /**
     * Выполнение логики команды.
     * @param request аргумент команды, который является ключом элемента в коллекции.
     */
    @Override
    public Response execute(Request request) {
        String key = request.getArgument();
        String login = request.getLogin();
        LabWork newElement = request.getObjectArgument();

        //Базовые проверки
        if (key == null || key.isEmpty()) {
            return new Response("\u001B[31mОшибка\u001B[0m: Введите ключ (ID) после команды!", false);
        }

        if (newElement == null) {
            return new Response("\u001B[31mОшибка\u001B[0m: Данные объекта не получены", false);
        }

        //Ищем старый элемент в памяти
        LabWork oldElement = collectionManager.getCollection().get(key);

        if (oldElement == null) {
            return new Response("\u001B[31mОшибка\u001B[0m: Элемент с ключом " + key + " не найден!", false);
        }

        //ПРОВЕРКА ВЛАДЕЛЬЦА
        if (!oldElement.getOwnerLogin().equals(login)) {
            return new Response("\u001B[31mОшибка\u001B[0m: Вы не являетесь владельцем этого объекта. Замена запрещена.", false);
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

                return new Response("Успех: Новый элемент больше старого. Замена произведена в БД и памяти.", true);
            } else {
                return new Response("\u001B[31mОшибка\u001B[0m: Не удалось обновить запись в базе данных.", false);
            }
        } else {
            return new Response("Замена не произведена: Новый элемент меньше или равен старому.", true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Заменяет значение по ключу, если новое значение больше старого";
    }

}
