package commands;

import managers.CollectionManager;
import managers.LabWorkAsker;
import models.LabWork;

public class RemoveLowerCommand implements Command{
    private final CollectionManager collectionManager;
    private final LabWorkAsker asker;


    public RemoveLowerCommand(CollectionManager collectionManager, LabWorkAsker asker) {
        this.collectionManager = collectionManager;
        this.asker = asker;
    }

    @Override
    public void execute(String arg) {
        System.out.println("Создание временного объекта-эталона для сравнения:");

        //ставим нулевой айдишник, так как сам элемент не будет входить в коллекцию
        LabWork ref = asker.createLabWork(0);

        int sizeBefore = collectionManager.getCollection().size();

        collectionManager.getCollection().entrySet().removeIf(entry -> entry.getValue().compareTo(ref) < 0);

        int removedCount = sizeBefore - collectionManager.getCollection().size();

        System.out.println("Команда выполнена успешно: Было удалено " + removedCount + " элементов!");

    }

    @Override
    public String getDescription() {
        return "Удаляет из коллекции все элементы, меньше заданного";
    }
}
