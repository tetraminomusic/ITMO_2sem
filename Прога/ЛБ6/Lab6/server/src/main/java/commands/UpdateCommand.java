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
            return new Response("Ошибка: не указан ID.", false);
        }

        try {
            int id = Integer.parseInt(arg);

            var entry = collectionManager.getCollection().entrySet().stream()
                    .filter(e -> e.getValue().getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (entry == null) {
                return new Response("Элемент с ID " + id + " не найден.", false);
            }

            String key = entry.getKey();
            LabWork oldLab = entry.getValue();

            LabWork updated = request.getObjectArgument();
            if (updated == null) {
                return new Response("ID подтвержден", true);
            }
            updated.setID(id);
            updated.setCreationDate(oldLab.getCreationDate());

            collectionManager.getCollection().put(key, updated);

            return new Response("Элемент [ID:" + id + "] успешно обновлен", true);

        } catch (NumberFormatException e) {
            return new Response("Ошибка: ID должен быть числом!", false);
        }
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