package commands;

import managers.CollectionManager;

/**
 * Команда, которая удаляет элемент коллекции по его ключу.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class RemovekeyCommand implements Command {
    /**
     * Менеджер коллекции, из которого извлекается сама коллекция.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекции, из которой извлекается сама коллекция
     */
    public RemovekeyCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды.
     * @param arg аргумент команды, являющийся ID элемента, который мы хотим удалить из коллекции.
     */
    @Override
    public void execute(String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: введите ключ после названия команды!");
            return;
        }
        if (collectionManager.getCollection().containsKey(arg)) {
            collectionManager.getCollection().remove(arg);
            System.out.println("Элемент с ключом " + arg + " был успешно удалён");
        } else {
            System.out.println("\u001B[31mОшибка\u001B[0m: элемент с ключом " + arg + " не был найден!");

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Удаляет элемент из коллекции по его ключу";
    }
}
