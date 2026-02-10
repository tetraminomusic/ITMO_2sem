package commands;

import managers.CollectionManager;
import managers.LabWorkAsker;
import models.LabWork;

import java.util.Map;

public class UpdateCommand  implements Command{
    private final CollectionManager collectionManager;
    private final LabWorkAsker asker;

    public UpdateCommand(CollectionManager collectionManager, LabWorkAsker asker) {
        this.collectionManager = collectionManager;
        this.asker = asker;
    }

    @Override
    public void execute(String arg) {
        try {
            int id = Integer.parseInt(arg);

            // TODO: Надо эту вещь заботать (стримы)
            String key = collectionManager.getCollection().entrySet().stream() //берём набор записей, превращаем в поток записей
                    .filter(entry -> entry.getValue().getId().equals(id)) // фильтруем по приципу: оставим только тот ID, который совпадает с моим
                    .map(Map.Entry :: getKey) // достаём ключ этой записи
                    .findFirst().orElse(null); //берём первый, иначе ничего не берём
            if (key != null) {
                LabWork updated = asker.createLabWork(id);

                //put позволяет удалить старый объект, если мы обращаемся по тому же ключу
                collectionManager.getCollection().put(key, updated);
                System.out.println("Объект успешно обновлён");
            }
        } catch (NumberFormatException e) {System.out.println("Ошибка: ID должен быть быть числом!");}


    }

    @Override
    public String getDescription() {
        return "Обновляет значение элемента коллекции по ID";
    }
}
