package commands;

import managers.CollectionManager;

public class ClearCommand implements Command{
    private final CollectionManager collectionManager;

    public ClearCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String arg) {
        collectionManager.clear();
    }

    @Override
    public String getDescription() {
        return "Очищает коллекцию";
    }
}
