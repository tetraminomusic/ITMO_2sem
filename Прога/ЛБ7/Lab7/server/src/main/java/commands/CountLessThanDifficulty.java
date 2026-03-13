package commands;

import managers.CollectionManager;
import models.Difficulty;
import network.Request;
import network.Response;

/**
 * Команда, выводящая количество элементов, значение поля difficulty которых меньше заданного
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class CountLessThanDifficulty implements Command{
    /** Менеджер коллекций, к которому обращается команда для вывода элементов коллекции */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции, из которой берём нужные нам элементы
     */
    public CountLessThanDifficulty(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполнение логики команды
     * @param request строковое представление сложности (константа из enum Difficulty)
     */
    @Override
    public Response execute(Request request) {
        String arg = request.getArgument();
        if (arg == null || arg.isEmpty()) {
            return new Response("\u001B[31mОшибка\u001B[0m: Введите значение сложности из списка: VERY_EASY, HARD, HOPELESS", false);
        }

        try {
            Difficulty target = Difficulty.valueOf(arg.toUpperCase());
            //Сравнием порядковые номера в enum
            long count = collectionManager.getCollection().values().stream()
                    .filter(lw -> lw.getDifficulty().ordinal() < target.ordinal())
                    .count();
            return new Response("Количество элементов со сложностью ниже + " + target + ": " + count, true);
        } catch (IllegalArgumentException e) {
            return new Response("\u001B[31mОшибка\u001B[0m: Такой сложности не существует в списке!", false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Выводит количество элементов, значение difficulty которых меньше заданного";
    }
}
