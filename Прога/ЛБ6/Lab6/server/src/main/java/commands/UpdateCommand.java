package commands;

import managers.CollectionManager;
import models.LabWork;
import network.Request;
import network.Response;

/**
 * Команда, которая обновляет значение элемента по ключу.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class UpdateCommand implements Command{
    /**
     * Менеджер коллекции, который управляет CRUD операциями.
     */
    private final CollectionManager collectionManager;


    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекции для выполнения операций
     */
    public UpdateCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды.
     * @param request аргумент команды, являющийся ID элемента, значение которого мы хотим обновить.
     */
    @Override
    public Response execute(Request request) {
        String arg = request.getArgument();
        if (arg == null || arg.isEmpty()) {
            return new Response("\u001B[31mОшибка\u001B[0m: Введите ID элемента для обновления", false);
        }

        try {
            int id = Integer.parseInt(arg);

            //проверяем существование элемента с таким ID
            if (!collectionManager.containsId(id)) {
                return new Response("\u001B[31mОшибка\u001B[0m: Элемент с ID " + id + " не найден в коллекции", false);
            }

            //получаем старый элемент для сохранения его ID (хотя мы и так передаём тот же ID)
            LabWork oldLab = collectionManager.getById(id);
            String key = findKeyById(id); // Находим ключ по ID

            if (key == null) {
                return new Response("\u001B[31mОшибка\u001B[0m: Не удалось найти ключ для элемента с ID " + id, false);
            }

            // Создаём обновлённый элемент с тем же ID
            LabWork updated = request.getObjectArgument();

            //ОБНОВА
            boolean updatedSuccessfully = collectionManager.update(key, updated);
            if (updatedSuccessfully) {
                return new Response("Элемент с ID " + id + " успешно обновлён", true);
            } else {
                return new Response("\u001B[31mОшибка\u001B[0m: Не удалось обновить элемент", false);
            }
        } catch (NumberFormatException e) {
            return new Response("\u001B[31mОшибка\u001B[0m: ID должен быть числом!", false);
        }
    }

    /**
     * Вспомогательный метод для поиска ключа по ID.
     * @param id идентификатор элемента
     * @return ключ элемента или null, если не найден
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