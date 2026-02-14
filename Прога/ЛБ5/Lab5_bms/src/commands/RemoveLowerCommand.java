package commands;

import managers.CollectionManager;
import managers.LabWorkAsker;
import models.LabWork;

/**
 * Команда, которая удаляет все элементы из коллекции, меньше заданного.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class RemoveLowerCommand implements Command{
    /**
     * Менеджер коллекции, из которой извлекают саму коллекцию элементов.
     */
    private final CollectionManager collectionManager;
    /**
     * Интерфейс запроса данных в консольном приложении для последующего создания элемента в коллекции.
     */
    private final LabWorkAsker asker;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции, из которой извлекают саму коллекцию элементов
     * @param asker интерфейс запроса данных в консольном приложении для последующего создания элемента в коллекции.
     */
    public RemoveLowerCommand(CollectionManager collectionManager, LabWorkAsker asker) {
        this.collectionManager = collectionManager;
        this.asker = asker;
    }

    /**
     * Выполнение логики команды.
     * @param arg аргумент команды. Не используется в данном методе.
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Удаляет из коллекции все элементы, меньше заданного";
    }
}
