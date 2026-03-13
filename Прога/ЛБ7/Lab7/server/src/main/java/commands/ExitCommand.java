package commands;

import network.Request;
import network.Response;

/**
 * Команда, которая осуществляет выход из консольного приложения (Устарело).
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 * @deprecated Данная команда используется только на стороне клиента
 */
public class ExitCommand implements Command {
    /**
     * Конструктор команды.
     */
    public ExitCommand() {
    }
    /**
     * Выполнение логики команды.
     * @param response аргумент команды. Не используется в данной команде.
     */
    @Override
    public Response execute(Request request) {
        return new Response("Завершение работы программы", true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Завершает работу программы (без сохранения в файл)";
    }
}
