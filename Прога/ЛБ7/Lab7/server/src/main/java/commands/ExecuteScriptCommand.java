package commands;

import managers.CommandManager;
import network.Request;
import network.Response;

import java.util.HashSet;
import java.util.Set;

public class ExecuteScriptCommand implements Command{

    public ExecuteScriptCommand() {
    }


    @Override
    public Response execute(Request request) {
        return new Response("Запуск скрипта на стороне клиента", true);
    }

    @Override
    public String getDescription() {
        return "Исполнение файла-скрипта";
    }
}
