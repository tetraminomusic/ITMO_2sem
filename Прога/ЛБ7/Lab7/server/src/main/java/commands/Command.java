package commands;

import network.Request;
import network.Response;

public interface Command {
    Response execute(Request request);

    String getDescription();

}
