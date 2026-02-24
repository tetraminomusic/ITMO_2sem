package commands;

import managers.CollectionManager;

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
     * @param arg аргумент команды. Не используется в данной команде.
     */
    @Override
    public void execute(String arg) {
        commands.forEach((name, command) -> System.out.printf("%-36s | %s%n", name, command.getDescription()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Выводит справку по всем командам";
    }
}
