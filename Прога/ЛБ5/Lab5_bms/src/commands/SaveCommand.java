package commands;

import managers.CollectionManager;
import managers.FileManager;

public class SaveCommand implements Command{
    private final CollectionManager collectionManager;
    private final FileManager fileManager;

    public SaveCommand(CollectionManager collectionManager, FileManager fileManager) {
        this.collectionManager = collectionManager;
        this.fileManager = fileManager;
    }

    @Override
    public void execute(String arg) {
        fileManager.write(collectionManager.getCollection());
    }

    @Override
    public String getDescription() {
        return "Сохраняет коллекцию в файл";
    }
}
