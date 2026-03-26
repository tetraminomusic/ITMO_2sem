import action.executeScriptAction.ExecuteScriptCommand;
import managers.UDPClient;
import managers.LabWorkAsker;
import models.LabWork;
import network.Request;
import network.Response;
import org.jline.reader.*;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Класс, инкапсулирующий логику работы консольного клиента.
 * Управляет авторизацией и бесконечным циклом отправки команд на сервер.
 */
public class ClientApp {
    private final UDPClient udpClient;
    private final LineReader lineReader;
    private final LabWorkAsker asker;
    private final ExecuteScriptCommand scriptExecutor;
    private final List<String> commandsWithObject;

    private String login;
    private String password;

    public ClientApp(UDPClient udpClient, LineReader lineReader, LabWorkAsker asker,
                     ExecuteScriptCommand scriptExecutor, List<String> commandsWithObject) {
        this.udpClient = udpClient;
        this.lineReader = lineReader;
        this.asker = asker;
        this.scriptExecutor = scriptExecutor;
        this.commandsWithObject = commandsWithObject;
    }

    /**
     * Запуск приложения (авторизация + главный цикл).
     */
    public void start() {
        if (!authenticate()) {
            return; // Если пользователь прервал авторизацию, выходим
        }

        runCommandLoop();
    }

    /**
     * Цикл авторизации пользователя.
     * @return true, если авторизация успешна.
     */
    private boolean authenticate() {
        System.out.println("Авторизация в систему");
        while (true) {
            try {
                login = lineReader.readLine("Введите логин: ").trim();
                password = lineReader.readLine("Введите пароль: ", '*').trim();

                if (login.isEmpty() || password.isEmpty()) {
                    System.out.println("\u001B[31mОшибка:\u001B[0m Логин и пароль не могут быть пустыми!");
                    continue;
                }

                Request authRequest = new Request("auth", "", null, login, password);
                udpClient.sendRequest(authRequest);
                Response authResponse = udpClient.receiveResponse();

                if (authResponse.getSuccess()) {
                    System.out.println("\u001B[32mУспех:\u001B[0m " + authResponse.getMessage());
                    return true;
                } else {
                    System.out.println("\u001B[31mОшибка авторизации:\u001B[0m Неверный пароль. Попробуйте еще раз.");
                }

            } catch (SocketTimeoutException e) {
                System.out.println("\u001B[31mСетевая ошибка:\u001B[0m Сервер не отвечает. Проверьте подключение и попробуйте снова.");
            } catch (IOException e) {
                System.out.println("\u001B[31mОшибка связи:\u001B[0m " + e.getMessage());
            } catch (UserInterruptException e) {
                System.out.println("\u001B[33mВвод прерван пользователем.\u001B[0m");
                return false;
            } catch (EndOfFileException e) {
                System.out.println("\nЗавершение сеанса...");
                return false;
            }
        }
    }

    /**
     * Главный цикл чтения и отправки команд.
     */
    private void runCommandLoop() {
        while (true) {
            try {
                String input = lineReader.readLine("\u001B[32mКонсольНоУжеКлиент@Что-то_заUмное2:~₽\u001B[0m ");
                if (input == null || input.trim().isEmpty()) continue;

                String[] tokens = input.trim().split("\\s+", 2);
                String commandName = tokens[0].toLowerCase();
                String argument = (tokens.length > 1) ? tokens[1] : "";

                if (commandName.equals("exit")) {
                    handleExit();
                    break;
                }

                if (commandName.equals("save")) {
                    System.out.println("\u001B[31mОшибка:\u001B[0m Команда 'save' доступна только на сервере.");
                    continue;
                }

                if (commandName.equals("execute_script")) {
                    handleExecuteScript(argument);
                    continue;
                }

                if (commandName.equals("update")) {
                    handleUpdate(commandName, argument);
                    continue;
                }

                // Обычная отправка команд
                sendStandardCommand(commandName, argument);

            } catch (EndOfFileException e) {
                System.out.println("\nЗавершение работы программы...");
                break;
            } catch (UserInterruptException e) {
                System.out.println("\u001B[33mНе ломайте программу бедного студента, пожалуйста\u001B[0m");
            }
        }
    }

    // --- Вспомогательные методы для разгрузки главного цикла ---

    private void handleExit() {
        try {
            udpClient.sendRequest(new Request("exit", "", null, login, password));
        } catch (IOException ignored) {}
        System.out.println("Завершение работы...");
    }

    private void handleExecuteScript(String argument) {
        try {
            udpClient.sendRequest(new Request("execute_script", argument, null, login, password));
            udpClient.receiveResponse();
            scriptExecutor.execute(argument, login, password, null);
        } catch (IOException e) {
            System.out.println("\u001B[31mОшибка:\u001B[0m связь неисправна при запуске скрипта");
        }
    }

    private void handleUpdate(String commandName, String argument) {
        if (argument.isEmpty()) {
            System.out.println("Укажите ID.");
            return;
        }
        try {
            Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            System.out.println("ID должен быть числом.");
            return;
        }

        try {
            udpClient.sendRequest(new Request(commandName, argument, null, login, password));
            Response checkResponse = udpClient.receiveResponse();

            if (!checkResponse.getSuccess()) {
                System.err.println(checkResponse.getMessage());
                return;
            }

            System.out.println("Введите новые данные для объекта:");
            LabWork updatedLab = asker.createLabWork();

            udpClient.sendRequest(new Request(commandName, argument, updatedLab, login, password));
            Response finalResponse = udpClient.receiveResponse();
            System.out.println(finalResponse.getMessage());

        } catch (IOException e) {
            System.err.println("Ошибка связи: " + e.getMessage());
        }
    }

    private void sendStandardCommand(String commandName, String argument) {
        Request request;
        try {
            if (commandsWithObject.contains(commandName)) {
                System.out.println("Команда '" + commandName + "' требует дополнительного ввода данных.");
                LabWork labWork = asker.createLabWork();
                request = new Request(commandName, argument, labWork, login, password);
            } else {
                request = new Request(commandName, argument, null, login, password);
            }
        } catch (Exception e) {
            System.out.println("\u001B[31mОшибка при формировании данных:\u001B[0m " + e.getMessage());
            return;
        }

        try {
            udpClient.sendRequest(request);
            Response response = udpClient.receiveResponse();
            if (response.getSuccess()) {
                System.out.println(response.getMessage());
            } else {
                System.out.println("\u001B[33mСервер:\u001B[0m " + response.getMessage());
            }
        } catch (SocketTimeoutException e) {
            System.out.println("\u001B[31mСетевая ошибка:\u001B[0m Сервер не ответил в течение 3 секунд.");
        } catch (PortUnreachableException e) {
            System.out.println("\u001B[31mСетевая ошибка:\u001B[0m Порт сервера недоступен.");
        } catch (IOException e) {
            System.out.println("\u001B[31mОшибка ввода/вывода при обмене по сети:\u001B[0m " + e.getMessage());
        }
    }
}