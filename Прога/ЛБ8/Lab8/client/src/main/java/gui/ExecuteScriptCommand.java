package gui;

import managers.UDPClient;
import managers.LabWorkAsker;
import network.Request;
import network.Response;
import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class ExecuteScriptCommand {
    private final UDPClient udpClient;
    private final LabWorkAsker asker;
    private final List<String> objectCommands;

    public ExecuteScriptCommand(UDPClient udpClient, LabWorkAsker asker, List<String> objectCommands) {
        this.udpClient = udpClient;
        this.asker = asker;
        this.objectCommands = objectCommands;
    }

    public void execute(String path, String login, String password, Consumer<String> logger) {
        File file = new File(path);
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] tokens = line.split("\\s+", 2);
                String cmd = tokens[0].toLowerCase();
                String arg = tokens.length > 1 ? tokens[1] : "";

                if (logger != null) logger.accept("Выполняю команду: " + cmd);

                Request request;
                if (objectCommands.contains(cmd)) {
                    asker.setScriptScanner(scanner); // Считываем объект из того же файла
                    request = new Request(cmd, arg, asker.createLabWork(), login, password);
                    asker.setScriptScanner(null);
                } else {
                    request = new Request(cmd, arg, null, login, password);
                }

                udpClient.sendRequest(request);
                Response response = udpClient.receiveResponse();

                if (logger != null) logger.accept("Результат: " + response.getMessage());
            }
        } catch (Exception e) {
            if (logger != null) logger.accept("Ошибка скрипта: " + e.getMessage());
        }
    }
}