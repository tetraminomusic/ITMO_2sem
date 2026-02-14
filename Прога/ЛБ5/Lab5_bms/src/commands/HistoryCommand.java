package commands;

import managers.CommandManager;

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
     * @param arg входной параметр. Не используется в данной команде
     */
    @Override
    public void execute(String arg) {
        if (history.isEmpty()) {
            System.out.println("История команд пуста!");
        } else {
            System.out.println("Список последних использованных команд:");
            for (String cmd : history) {
                System.out.println("- " + cmd);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Вывводит список последних 14 использованных команд";
    }



}
