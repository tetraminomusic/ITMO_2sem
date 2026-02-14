package commands;

import managers.CollectionManager;

import static java.util.Map.Entry.comparingByValue;

/**
 * Команда, которая выводит в консоль все элементы коллекции.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class ShowCommand implements Command{
    /**
     * Менеджер коллекции, из которого извлекается сама коллекция.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекции, из которого извлекается сама коллекция
     */
    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды.
     * @param arg аргумент команды. Не используется в данной команде.
     */
    @Override
    public void execute(String arg) {
        var collection = collectionManager.getCollection();

        if (collection.isEmpty()) {
            System.out.println("Коллекция пуста");
            return;
        }
        //когда мы вызываем value, мы вызываем метод to String, который мы переопределили
        // TODO: разобраться в этом коде
        collection.entrySet().stream().sorted(comparingByValue()).forEach(entry ->
                System.out.println("Ключ: " + entry.getKey() + " | " + entry.getValue()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Выводит все элементы коллекции в строковом представлении";
    }
}
