package commands;

import managers.CommandManager;

import java.util.List;

public class HistoryCommand implements Command{
    private final List<String> history;

    public HistoryCommand(List<String> history) {
        this.history = history;
    }

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

    @Override
    public String getDescription() {
        return "Вывводит список последних 14 использованных команд";
    }



}
