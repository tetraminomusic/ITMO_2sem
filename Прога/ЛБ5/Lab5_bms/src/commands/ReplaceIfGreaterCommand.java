package commands;

import managers.CollectionManager;
import managers.LabWorkAsker;
import models.LabWork;

public class ReplaceIfGreaterCommand implements Command{
    private final CollectionManager collectionManager;
    private final LabWorkAsker asker;

    public ReplaceIfGreaterCommand(CollectionManager collectionManager, LabWorkAsker asker) {
        this.collectionManager = collectionManager;
        this.asker = asker;
    }

    @Override
    public void execute(String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("Ошибка: Введите ключ после названия команды!");
            return;
        }

        if (!collectionManager.getCollection().containsKey(arg)) {
            System.out.println("Ошибка: Элемент с ключом " + arg + " не найден!");
            return;
        }

        LabWork newElement = asker.createLabWork(collectionManager.generateNextId());
        LabWork oldElement = collectionManager.getCollection().get(arg);

        // Сравнение относительно имен (поменять можно в классе LabWork)
        if (newElement.compareTo(oldElement) > 0) {
            collectionManager.getCollection().put(arg, newElement);
            System.out.println("Новый элемент больше старого - Замена произведена!");
        } else {
            System.out.println("Новый элемент меньше старого - Замена не произведена!");
        }
    }

    @Override
    public String getDescription() {
        return "Заменяет значение по ключу, если новое значение больше старого";
    }

}
