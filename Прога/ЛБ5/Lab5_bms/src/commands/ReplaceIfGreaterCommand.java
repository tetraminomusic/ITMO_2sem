package commands;

import managers.CollectionManager;
import managers.LabWorkAsker;
import models.LabWork;

/**
 * Команда, которая заменяет значение по ключу, если новое значение больше старого.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class ReplaceIfGreaterCommand implements Command{
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
     * @param collectionManager менеджер коллекции, из которой извлекают саму коллекцию элементов.
     * @param asker интерфейс запроса данных в консольном приложении для последующего создания элемента в коллекции.
     */
    public ReplaceIfGreaterCommand(CollectionManager collectionManager, LabWorkAsker asker) {
        this.collectionManager = collectionManager;
        this.asker = asker;
    }

    /**
     * Выполнение логики команды.
     * @param arg аргумент команды, который является ключом элемента в коллекции.
     */
    @Override
    public void execute(String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Введите ключ после названия команды!");
            return;
        }

        if (!collectionManager.getCollection().containsKey(arg)) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Элемент с ключом " + arg + " не найден!");
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Заменяет значение по ключу, если новое значение больше старого";
    }

}
