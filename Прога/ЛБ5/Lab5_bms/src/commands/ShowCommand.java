package commands;

import managers.CollectionManager;

import static java.util.Map.Entry.comparingByValue;

public class ShowCommand implements Command{
    private final CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

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

    @Override
    public String getDescription() {
        return "Выводит все элементы коллекции в строковом представлении";
    }
}
