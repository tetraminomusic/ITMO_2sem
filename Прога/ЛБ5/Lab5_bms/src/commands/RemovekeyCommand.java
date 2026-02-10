package commands;

import managers.CollectionManager;

public class RemovekeyCommand implements Command {
    private final CollectionManager collectionManager;

    public RemovekeyCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

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

    @Override
    public String getDescription() {
        return "Удаляет элемент из коллекции по его ключу";
    }
}
