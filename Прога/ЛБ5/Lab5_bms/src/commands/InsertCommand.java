package commands;

import managers.CollectionManager;
import managers.LabWorkAsker;
import models.LabWork;

/**
 * Команда, которая добавляет новый элемент в коллекцию с заданным ключом.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class InsertCommand implements Command{
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
     * @param labWorkAsker интерфейс запроса данных и создания нового элемента коллекции
     */
    public InsertCommand(CollectionManager collectionManager, LabWorkAsker labWorkAsker) {
        this.collectionManager = collectionManager;
        asker = labWorkAsker;
    }

    /**
     * Выполнение логики команды
     * @param arg - аргумент команды, который является ID для нового элемента коллекции.
     */
    @Override
    public void execute(String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Введите ключ (имя) для нового элемента");
            return;
        }

        if (collectionManager.getCollection().containsKey(arg)) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Элемент с ключом " + arg + " уже существует!");
            return;
        }

        Integer newId = collectionManager.generateNextId();

        LabWork newLab = asker.createLabWork(newId);

        collectionManager.getCollection().put(arg, newLab);
        System.out.println("Элемент был успешно добавлен");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Добавляет новый элемент с заданным ключом";
    }
}
