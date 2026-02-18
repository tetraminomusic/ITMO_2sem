package commands;

import managers.CollectionManager;
import managers.LabWorkAsker;
import models.LabWork;

import java.util.Map;

/**
 * Команда, которая обновляет значение элемента по ключу.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class UpdateCommand  implements Command{
    /**
     * Менеджер коллекции, из которого извлекается сама коллекция.
     */
    private final CollectionManager collectionManager;
    /**
     * Интерфейс, который запрашивает данные и создаёт новый элемент коллекции.
     */
    private final LabWorkAsker asker;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекции, из которого извлекается сама коллекция
     * @param asker интерфейс, который запрашивает данные и создаёт новый элемент коллекции
     */
    public UpdateCommand(CollectionManager collectionManager, LabWorkAsker asker) {
        this.collectionManager = collectionManager;
        this.asker = asker;
    }

    /**
     * Выполнение логики команды.
     * @param arg аргумент команды, являющийся ключом элемента, значение которого мы хотим обновить.
     */
    @Override
    public void execute(String arg) {
        try {
            int id = Integer.parseInt(arg);

            // TODO: Надо эту вещь заботать (стримы)
            String key = collectionManager.getCollection().entrySet().stream() //берём набор записей, превращаем в поток записей
                    .filter(entry -> entry.getValue().getId().equals(id)) // фильтруем по приципу: оставим только тот ID, который совпадает с моим
                    .map(entry -> entry.getKey()) // достаём ключ этой записи
                    .findFirst().orElse(null); //берём первый, иначе ничего не берём
            if (key != null) {
                LabWork updated = asker.createLabWork(id);

                //put позволяет удалить старый объект, если мы обращаемся по тому же ключу
                collectionManager.getCollection().put(key, updated);
                System.out.println("Объект успешно обновлён");
            }
        } catch (NumberFormatException e) {System.out.println("\u001B[31mОшибка\u001B[0m: ID должен быть быть числом!");}


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Обновляет значение элемента коллекции по ID";
    }
}
