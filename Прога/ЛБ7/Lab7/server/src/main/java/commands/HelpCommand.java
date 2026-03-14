package commands;

import network.Request;
import network.Response;

import java.util.Map;

public class HelpCommand implements Command{
    private final Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public Response execute(Request request) {
        StringBuilder result = new StringBuilder();
        commands.forEach((name, command) -> {
            String line = String.format("%-36s | %s%n", name, command.getDescription());
            result.append(line);
        });
        return new Response(result.toString(), true);
    }

    @Override
    public String getDescription() {
        return "Выводит справку по всем командам";
    }
}
