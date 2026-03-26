package action.executeScriptAction;

import managers.UDPClient;
import managers.LabWorkAsker;
import network.Request;
import network.Response;
import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Класс-исполнитель скриптов.
 * Читает файл и отправляет запросы на сервер, уведомляя вызывающий код о прогрессе.
 */
public class ExecuteScriptCommand {
    private final UDPClient udpClient;
    private final LabWorkAsker asker;
    private final List<String> objectCommands;

    public ExecuteScriptCommand(UDPClient udpClient, LabWorkAsker asker, List<String> objectCommands) {
        this.udpClient = udpClient;
        this.asker = asker;
        this.objectCommands = objectCommands;
    }

    /**
     * Выполняет скрипт.
     * @param logger принимает (имя_команды, ответ_сервера). Если ответ null - значит команда только началась.
     */
    public void execute(String path, String login, String password, BiConsumer<String, Response> logger) {
        File file = new File(path);
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] tokens = line.split("\\s+", 2);
                String cmd = tokens[0].toLowerCase();
                String arg = tokens.length > 1 ? tokens[1] : "";

                // 1. Уведомляем логгер, что команда НАЧАЛАСЬ (передаем null в качестве ответа)
                if (logger != null) logger.accept(cmd, null);

                Request request;
                if (objectCommands.contains(cmd)) {
                    asker.setScriptScanner(scanner);
                    request = new Request(cmd, arg, asker.createLabWork(), login, password);
                    asker.setScriptScanner(null);
                } else {
                    request = new Request(cmd, arg, null, login, password);
                }

                // 2. Отправляем и получаем ответ
                udpClient.sendRequest(request);
                Response response = udpClient.receiveResponse();

                // 3. Уведомляем логгер о РЕЗУЛЬТАТЕ (передаем объект ответа)
                if (logger != null) logger.accept(cmd, response);
            }
        } catch (Exception e) {
            // В случае критической ошибки (например, файл не найден) шлем фейковый ответ с ошибкой
            if (logger != null) logger.accept("error", new Response(e.getMessage(), false, null));
        }
    }
}