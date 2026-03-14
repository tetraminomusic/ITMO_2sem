package commands;

import network.Request;
import network.Response;

import java.util.List;

public class HistoryCommand implements Command{
    private final List<String> history;

    public HistoryCommand(List<String> history) {
        this.history = history;
    }

    @Override
    public Response execute(Request request) {
        if (history.isEmpty()) {
            return new Response("История команд пуста!", true);
        } else {
            StringBuilder result = new StringBuilder();
            result.append("Список последних использованных команд:\n");
            for (String cmd : history) {
                result.append("- " + cmd).append("\n");
            }

            return new Response(result.toString(), true);
        }
    }

    @Override
    public String getDescription() {
        return "Выводит список последних 14 использованных команд";
    }



}
