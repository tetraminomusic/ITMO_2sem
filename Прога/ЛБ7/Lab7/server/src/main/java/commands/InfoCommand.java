package commands;

import managers.CollectionManager;
import network.Request;
import network.Response;

/**
 * Команда, которая выводит информацию о коллекции.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class InfoCommand implements Command{
    /**
     * Менеджер коллекции, из которого извлекается сама коллекция.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекции, из которого извлекается сама коллекция
     */
    public InfoCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды.
     * @param request - входной параметр. Не используется в данной команде.
     */
    @Override
    public Response execute(Request request) {
        String infoText = collectionManager.info();
        return new Response(infoText, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Вывод информации о коллекции";
    }
}
