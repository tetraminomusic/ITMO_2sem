package commands;

import managers.CollectionManager;

public class InfoCommand implements Command{
    private final CollectionManager collectionManager;

    public InfoCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String arg) {
        System.out.println(collectionManager.info());
    }

    @Override
    public String getDescription() {
        return "Вывод информации о коллекции";
    }
}
