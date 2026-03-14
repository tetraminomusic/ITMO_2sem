package commands;

import managers.CollectionManager;
import network.Request;
import network.Response;


public class SaveCommand implements Command{
    private final CollectionManager collectionManager;

    public SaveCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        return null;
    }
    @Override
    public String getDescription() {
        return "Сохраняет коллекцию в файл";
    }
}
