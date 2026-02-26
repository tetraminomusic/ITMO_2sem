package commands;
import managers.CollectionManager;
import network.Request;
import network.Response;

/**
 * Команда очистки коллекции, то есть удаление всех ещё элементов
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */

public class ClearCommand implements Command{
    /** Менеджер коллекции, к которому обращается команда для выполнения очистки. */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции, которая будет очищена
     */
    public ClearCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды.
     * @param request не используется в данной команде
     */
    @Override
    public Response execute(Request request) {
        try {
            collectionManager.clear();
            return new Response("Коллекция была очищена", true);
        } catch (Exception e) {
            return new Response("\u001B[31mОшибка:\u001B[0m Не удалось очистить коллекцию", false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Очищает коллекцию";
    }
}
