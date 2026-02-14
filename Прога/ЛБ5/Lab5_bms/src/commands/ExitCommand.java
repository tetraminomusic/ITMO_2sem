package commands;

import managers.CollectionManager;

/**
 * Команда, которая осуществляет выход из консольного приложения.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class ExitCommand implements Command {
    /**
     * Конструктор команды.
     */
    public ExitCommand() {
    }

    /**
     * Выполнение логики команды.
     * @param arg аргумент команды. Не используется в данной команде.
     */
    @Override
    public void execute(String arg) {
        System.out.println("Завершение работы программы...");
        System.exit(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Завершает работу программы (без сохранения в файл)";
    }
}
