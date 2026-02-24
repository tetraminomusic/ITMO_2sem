package commands;
import managers.CollectionManager;

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
     * @param arg не используется в данной команде
     */
    @Override
    public void execute(String arg) {
        collectionManager.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Очищает коллекцию";
    }
}
