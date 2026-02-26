package commands;

import managers.CollectionManager;
import managers.FileManager;
import network.Request;
import network.Response;

/**
 * Команда, которая сохраняет коллекцию в файл.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 * @deprecated Сохранением занимается сервер при закрытии приложения
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
     * @param request аргумент команды. Не используется в данной команде.
     */
    @Override
    public Response execute(Request request) {
        fileManager.write(collectionManager.getCollection());
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Сохраняет коллекцию в файл";
    }
}
