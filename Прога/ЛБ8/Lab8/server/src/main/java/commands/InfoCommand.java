package commands;

import managers.CollectionManager;
import network.Request;
import network.Response;

public class InfoCommand implements Command{
    private final CollectionManager collectionManager;
    public InfoCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        String infoText = collectionManager.info();
        return new Response(infoText, true, null);
    }

    @Override
    public String getDescription() {
        return "Вывод информации о коллекции";
    }
}
