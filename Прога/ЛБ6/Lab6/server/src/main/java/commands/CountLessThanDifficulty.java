package commands;

import managers.CollectionManager;
import models.Difficulty;

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
     * @param arg строковое представление сложности (константа из enum Difficulty)
     */
    @Override
    public void execute(String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Введите значение сложности из списка: VERY_EASY, HARD, HOPELESS");
            return;
        }

        try {
            Difficulty target = Difficulty.valueOf(arg.toUpperCase());
            //Сравнием порядковые номера в enum
            long count = collectionManager.getCollection().values().stream()
                    .filter(lw -> lw.getDifficulty().ordinal() < target.ordinal())
                    .count();
            System.out.println("Количество элементов со сложностью ниже + " + target + ": " + count);
        } catch (IllegalArgumentException e) {
            System.out.println("\u001B[31mОшибка\u001B[0m: Такой сложности не существует в списке!");
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
