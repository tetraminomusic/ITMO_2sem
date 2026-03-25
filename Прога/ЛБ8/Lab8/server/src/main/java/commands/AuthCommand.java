package commands;

import network.Request;
import network.Response;

public class AuthCommand implements Command {

    @Override
    public Response execute(Request request) {
        return new Response("Авторизация прошла успешно!", true, null);
    }

    @Override
    public String getDescription() {
        return "системная команда авторизации (не для вызова пользователем)";
    }
}