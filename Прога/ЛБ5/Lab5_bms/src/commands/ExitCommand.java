package commands;

import managers.CollectionManager;

public class ExitCommand implements Command {
    private final CollectionManager collectionManager;

    public ExitCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String arg) {
        System.out.println("Завершение работы программы...");
        System.exit(0);
    }

    @Override
    public String getDescription() {
        return "Завершает работы программы (без сохранения в файл)";
    }
}
