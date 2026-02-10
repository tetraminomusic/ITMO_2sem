package commands;

import managers.CollectionManager;
import managers.LabWorkAsker;
import models.LabWork;

public class InsertCommand implements Command{
    private final CollectionManager collectionManager;
    private final LabWorkAsker asker;

    public InsertCommand(CollectionManager collectionManager, LabWorkAsker labWorkAsker) {
        this.collectionManager = collectionManager;
        asker = labWorkAsker;
    }

    @Override
    public void execute(String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("Ошибка: Введите ключ (имя) для нового элемента");
            return;
        }

        if (collectionManager.getCollection().containsKey(arg)) {
            System.out.println("Ошибка: Элемент с ключом " + arg + " уже существует!");
            return;
        }

        Integer newId = collectionManager.generateNextId();

        LabWork newLab = asker.createLabWork(newId);

        collectionManager.getCollection().put(arg, newLab);
        System.out.println("Элемент был успешно добавлен");
    }

    @Override
    public String getDescription() {
        return "Добавляет новый элемент с заданным ключом";
    }
}
