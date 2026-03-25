package commands;

import managers.CollectionManager;
import managers.DatabaseManager;
import models.LabWork;
import network.Request;
import network.Response;

import java.util.Map;

public class UpdateCommand implements Command{
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager;

    public UpdateCommand(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    @Override
    public Response execute(Request request) {
        String arg = request.getArgument();
        String login = request.getLogin();

        if (arg == null || arg.isEmpty()) {
            return new Response("Ошибка: Введите ID элемента для обновления", false, null);
        }

        try {
            int id = Integer.parseInt(arg);

            Map.Entry<String, LabWork> entryToUpdate = collectionManager.getCollection().entrySet().stream()
                    .filter(entry -> entry.getValue().getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (entryToUpdate == null) {
                return new Response("Ошибка: Элемент с ID " + id + " не найден в коллекции", false, null);
            }

            LabWork oldLab = entryToUpdate.getValue();

            if (!oldLab.getOwnerLogin().equals(login)) {
                return new Response("Ошибка: Вы не являетесь владельцем данного объекта!", false, null);
            }

            LabWork updatedLab = request.getObjectArgument();

            if (updatedLab == null) {
                return new Response("ID и права доступа подтверждены.", true, null);
            }

            if (databaseManager.updateLabWork(id, updatedLab, login)) {
                updatedLab.setID(id);
                updatedLab.setOwnerLogin(login);
                updatedLab.setCreationDate(oldLab.getCreationDate());
                collectionManager.getCollection().put(entryToUpdate.getKey(), updatedLab);

                return new Response("Элемент с ID " + id + " успешно обновлен.", true, null);
            } else {
                return new Response("Ошибка: Не удалось обновить запись в БД", false, null);
            }

        } catch (NumberFormatException e) {
            return new Response("Ошибка: ID должен быть числом", false, null);
        } catch (Exception e) {
            return new Response("Ошибка на сервере: " + e.getMessage(), false, null);
        }
    }

    @Override
    public String getDescription() {
        return "update id {element} : обновить значение элемента коллекции по ID";
    }
}