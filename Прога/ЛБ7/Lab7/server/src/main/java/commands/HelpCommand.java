package commands;

import managers.CollectionManager;
import network.Request;
import network.Response;

import java.util.Map;

/**
 * Команда, которая выводит в консоль справку обо всех доступных командах.
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class HelpCommand implements Command{
    /**
     * Список команд.
     */
    private final Map<String, Command> commands;

    /**
     * Конструктор команды.
     * @param commands список команд
     */
    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    /**
     * Выполнение логики команды.
     * @param request аргумент команды. Не используется в данной команде.
     */
    @Override
    public Response execute(Request request) {
        StringBuilder result = new StringBuilder();
        commands.forEach((name, command) -> {
            //String.format делает то же самое, что и printf, только возвращает строку
            String line = String.format("%-36s | %s%n", name, command.getDescription());
            result.append(line);
        });
        return new Response(result.toString(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Выводит справку по всем командам";
    }
}
