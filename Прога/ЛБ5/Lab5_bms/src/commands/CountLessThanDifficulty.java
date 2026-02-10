package commands;

import managers.CollectionManager;
import models.Difficulty;

public class CountLessThanDifficulty implements Command{
    private final CollectionManager collectionManager;

    public CountLessThanDifficulty(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("Ошибка: Введите значение сложности из списка: VERY_EASY, HARD, HOPELESS");
        }

        try {
            Difficulty target = Difficulty.valueOf(arg.toUpperCase());
            //Сравнием порядковые номера в enum
            long count = collectionManager.getCollection().values().stream()
                    .filter(lw -> lw.getDifficulty().ordinal() < target.ordinal())
                    .count();
            System.out.println("Количество элементов со сложностью ниже + " + target + ": " + count);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: Такой сложности не существует в списке!");
        }
    }

    @Override
    public String getDescription() {
        return "Выводит количество элементов, значение difficulty которых меньше заданного";
    }
}
