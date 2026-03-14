package commands;

import managers.CollectionManager;
import models.Difficulty;
import network.Request;
import network.Response;


public class CountLessThanDifficulty implements Command{
    private final CollectionManager collectionManager;

    public CountLessThanDifficulty(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

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

    @Override
    public String getDescription() {
        return "Выводит количество элементов, значение difficulty которых меньше заданного";
    }
}
