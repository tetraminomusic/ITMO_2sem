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
     * Менеджер коллекции, который управляет CRUD операциями.
     */
    private final CollectionManager collectionManager;
    /**
     * Интерфейс, который запрашивает данные и создаёт новый элемент коллекции.
     */
    private final LabWorkAsker asker;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекции для выполнения операций
     * @param labWorkAsker интерфейс запроса данных и создания нового элемента коллекции
     */
    public InsertCommand(CollectionManager collectionManager, LabWorkAsker labWorkAsker) {
        this.collectionManager = collectionManager;
        this.asker = labWorkAsker;
    }

    /**
     * Выполнение логики команды
     * @param arg - аргумент команды, который является ключом для нового элемента коллекции.
     */
    @Override
    public void execute(String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Введите ключ для нового элемента");
            return;
        }

        //существует ли уже элемент с таким ключом через менеджер
        if (collectionManager.containsKey(arg)) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Элемент с ключом \"" + arg + "\" уже существует!");
            return;
        }

        //генерируем новый уникальный ID через менеджер
        Integer newId = collectionManager.generateNextId();

        //создаём новый объект LabWork через Asker
        LabWork newLab = asker.createLabWork(newId);

        //ОБНОВА
        boolean added = collectionManager.add(arg, newLab);

        if (added) {
            System.out.println("Элемент с ключом \"" + arg + "\" успешно добавлен в коллекцию");
        } else {
            System.out.println("\u001B[31mОшибка\u001B[0m: Не удалось добавить элемент");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "insert null {element} : добавить новый элемент с заданным ключом";
    }
}