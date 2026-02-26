package network;

import models.LabWork;
import java.io.Serializable;
import java.net.SocketAddress;

/**
 * Класс-запрос, объект которого используется для передачи данных по сети (имя команды, аргумент команды и объект коллекции)
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Имя команды, которую нужно выполнить
     */
    private final String commandName;

    /**
     * Аргумент команды, может быть null
     */
    private final String argument;

    /**
     * Сложный объект, если команда требует передачи нового объекта, например, в команде insert. Может быть null
     */
    private final LabWork objectArgument;

    /**
     * Адрес отправителя
     * Ключевое слово transient значит, что данное поле не будет передавать по сети
     * Оно нужно только серверу, чтобы запомнить, кому отправлять ответ по Response.
     */
    private transient SocketAddress clientAddress;

    /**
     * Конструктор класса-запроса
     * @param commandName Имя команды
     * @param argument Строковый аргумент команды. Может быть null
     * @param objectArgument Сложный объект, если команда требует передачи нового объекта коллекции. Может быть null.
     */
    public Request(String commandName, String argument, LabWork objectArgument) {
        this.commandName = commandName;
        this.argument = argument;
        this.objectArgument = objectArgument;
    }

    /**
     * Геттер для чтения имени команды.
     * @return имя команды
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Геттер для чтения строкового представления команды
     * @return аргумент команды
     */
    public String getArgument() {
        return argument;
    }

    /**
     * Геттер для чтения отправляемого по сети объекта коллекции
     * @return объект коллекции
     */
    public LabWork getObjectArgument() {
        return objectArgument;
    }

    /**
     * Сеттер для адреса клиента (Используется только на стороне сервера)
     * @param newAddress адрес клиента
     */
    public void setClientAddress(SocketAddress newAddress) {
        clientAddress = newAddress;
    }

    /**
     * Геттер для адреса клиента (Используется только на стороне сервера)
     * @return адрес клиента
     */
    public SocketAddress getClientAddress() {
        return clientAddress;
    }
}
