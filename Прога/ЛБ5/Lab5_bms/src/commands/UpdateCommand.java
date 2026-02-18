package commands;

import managers.CollectionManager;
import managers.LabWorkAsker;
import models.LabWork;

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
     * Интерфейс, который запрашивает данные и создаёт новый элемент коллекции.
     */
    private final LabWorkAsker asker;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекции для выполнения операций
     * @param asker интерфейс, который запрашивает данные и создаёт новый элемент коллекции
     */
    public UpdateCommand(CollectionManager collectionManager, LabWorkAsker asker) {
        this.collectionManager = collectionManager;
        this.asker = asker;
    }

    /**
     * Выполнение логики команды.
     * @param arg аргумент команды, являющийся ID элемента, значение которого мы хотим обновить.
     */
    @Override
    public void execute(String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Введите ID элемента для обновления");
            return;
        }

        try {
            int id = Integer.parseInt(arg);

            //проверяем существование элемента с таким ID
            if (!collectionManager.containsId(id)) {
                System.out.println("\u001B[31mОшибка\u001B[0m: Элемент с ID " + id + " не найден в коллекции");
                return;
            }

            //получаем старый элемент для сохранения его ID (хотя мы и так передаём тот же ID)
            LabWork oldLab = collectionManager.getById(id);
            String key = findKeyById(id); // Находим ключ по ID

            if (key == null) {
                System.out.println("\u001B[31mОшибка\u001B[0m: Не удалось найти ключ для элемента с ID " + id);
                return;
            }

            // Создаём обновлённый элемент с тем же ID
            LabWork updated = asker.createLabWork(id);

            //ОБНОВА
            boolean updatedSuccessfully = collectionManager.update(key, updated);
            if (updatedSuccessfully) {
                System.out.println("Элемент с ID " + id + " успешно обновлён");
            } else {
                System.out.println("\u001B[31mОшибка\u001B[0m: Не удалось обновить элемент");
            }

        } catch (NumberFormatException e) {
            System.out.println("\u001B[31mОшибка\u001B[0m: ID должен быть числом!");
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