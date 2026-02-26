package commands;

import managers.CommandManager;
import network.Request;
import network.Response;

import java.util.List;

/**
 * Команда, которая выводит последние 14 ранее использованных команд за текущую сессию.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class HistoryCommand implements Command{
    /**
     * Список, хранящий 14 последних использованных команд.
     */
    private final List<String> history;

    /**
     * Конструктор команды.
     * @param history список последних 14 использованных команд
     */
    public HistoryCommand(List<String> history) {
        this.history = history;
    }

    /**
     * Выполнение логики команды
     * @param request входной параметр. Не используется в данной команде
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Выводит список последних 14 использованных команд";
    }



}
