package commands;

import managers.CollectionManager;
import models.LabWork;
import network.Request;
import network.Response;

/**
 * Команда, которая удаляет все элементы из коллекции, меньше заданного.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class RemoveLowerCommand implements Command{
    /**
     * Менеджер коллекции, из которой извлекают саму коллекцию элементов.
     */
    private final CollectionManager collectionManager;
    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции, из которой извлекают саму коллекцию элементов
     */
    public RemoveLowerCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды.
     * @param request аргумент команды. Не используется в данном методе.
     */
    @Override
    public Response execute(Request request) {
        System.out.println("Создание временного объекта-эталона для сравнения:");

        //ставим нулевой айдишник, так как сам элемент не будет входить в коллекцию
        LabWork ref = request.getObjectArgument();

        if (ref == null) {
            return new Response("\u001B[31mОшибка\u001B[0m: Клиент не прислал объект для сравнения.", false);
        }

        int sizeBefore = collectionManager.getCollection().size();

        collectionManager.getCollection().entrySet().removeIf(entry -> entry.getValue().compareTo(ref) < 0);

        int removedCount = sizeBefore - collectionManager.getCollection().size();

        return new Response("Команда выполнена успешно: Было удалено " + removedCount + " элементов!", true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Удаляет из коллекции все элементы, меньше заданного";
    }
}
