package network;

import models.LabWork;

import java.io.Serial;
import java.io.Serializable;
import java.net.SocketAddress;

/**
 * Объект, который мы отправляем серверу.
 */
public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String commandName;
    private String arg;
    private LabWork objectArg;

    private transient SocketAddress clientAddress;

    public Request(String commandName, String arg, LabWork objectArg) {
        this.commandName = commandName;
        this.arg = arg;
        this.objectArg = objectArg;
    }

    public SocketAddress getClientAddress() {
        return clientAddress;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getArgument() {
        return arg;
    }

    public LabWork getObjectArgument() {
        return objectArg;
    }

    public void setClientAddress(SocketAddress clientAddress) {
        this.clientAddress = clientAddress;
    }
}
