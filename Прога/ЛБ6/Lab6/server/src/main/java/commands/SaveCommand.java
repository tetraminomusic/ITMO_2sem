package commands;

import managers.CollectionManager;
import managers.FileManager;

/**
 * Команда, которая сохраняет коллекцию в файл.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class SaveCommand implements Command{
    /**
     * Менеджер коллекции, из которого извлечём саму коллекцию.
     */
    private final CollectionManager collectionManager;
    /**
     * Менеджер файлов, нужный для чтения/записи файлов.
     */
    private final FileManager fileManager;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекции, из которого извлечём саму коллекцию
     * @param fileManager менеджер файлов, нужный для чтения/записи файлов
     */
    public SaveCommand(CollectionManager collectionManager, FileManager fileManager) {
        this.collectionManager = collectionManager;
        this.fileManager = fileManager;
    }

    /**
     * Выполнение логики команды
     * @param arg аргумент команды. Не используется в данной команде.
     */
    @Override
    public void execute(String arg) {
        fileManager.write(collectionManager.getCollection());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Сохраняет коллекцию в файл";
    }
}
