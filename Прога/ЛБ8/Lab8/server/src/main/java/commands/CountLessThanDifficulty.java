package commands;

import managers.CollectionManager;
import models.Difficulty;
import network.Request;
import network.Response;

/**
 * Команда для подсчета элементов, сложность которых ниже заданной.
 */
public class CountLessThanDifficulty implements Command {
    private final CollectionManager collectionManager;

    public CountLessThanDifficulty(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        String arg = request.getArgument();

        if (arg == null || arg.isEmpty()) {
            return new Response("server.msg.error_no_difficulty", false, null);
        }

        try {
            Difficulty target = Difficulty.valueOf(arg.toUpperCase());

            long count = collectionManager.getCollection().values().stream()
                    .filter(lw -> lw.getDifficulty().ordinal() < target.ordinal())
                    .count();

            return new Response("server.msg.count_diff_success", true, null, target, count);

        } catch (IllegalArgumentException e) {
            return new Response("server.msg.error_invalid_difficulty", false, null);
        }
    }

    @Override
    public String getDescription() {
        return "вывести количество элементов, значение difficulty которых меньше заданного";
    }
}