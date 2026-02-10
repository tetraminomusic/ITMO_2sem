package commands;

import managers.CollectionManager;

import java.util.Map;

public class HelpCommand implements Command{
    private final Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(String arg) {
        commands.forEach((name, command) -> System.out.printf("%-36s | %s%n", name, command.getDescription()));
    }

    @Override
    public String getDescription() {
        return "Выводит справку по всем командам";
    }
}
