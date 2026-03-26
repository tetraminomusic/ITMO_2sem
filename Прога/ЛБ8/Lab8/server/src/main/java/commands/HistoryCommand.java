package commands;

import network.Request;
import network.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда для получения истории последних выполненных команд.
 */
public class HistoryCommand implements Command {
    private final List<String> history;

    public HistoryCommand(List<String> history) {
        this.history = history;
    }

    @Override
    public Response execute(Request request) {
        if (history.isEmpty()) {
            return new Response("server.msg.history_empty", true, null);
        }

        String historyString = history.stream()
                .map(cmd -> "- " + cmd)
                .collect(Collectors.joining("\n"));

        return new Response("server.msg.history_success", true, null, historyString);
    }

    @Override
    public String getDescription() {
        return "вывести последние 14 команд (без их аргументов)";
    }
}