package commands;

import network.Request;
import network.Response;

/**
 * Базовый интерфейс для реализации команд в приложении.
 * Каждая команда должна реализовывать данный интерфейс
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public interface Command {
    /**
     *  Выполнение логики команды.
     *
     * @param request - принимает объект запроса
     */

    Response execute(Request request);

    /**
     * Краткое описание команды.
     * Используется для формирования справки в команде help.
     *
     * @return строковое описание команды.
     */
    String getDescription();

}
