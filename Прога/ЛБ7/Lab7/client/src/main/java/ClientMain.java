import managers.LabWorkAsker;

import managers.UDPClient;
import models.LabWork;
import network.*;
import org.jline.reader.*;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;


import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;


/**
 * Главный класс приложения.
 * Инициализирует основные компоненты системы, настраивает терминал JLine
 * и запускает интерактивный цикл обработки команд.
 *
 * @author Малых Кирилл Романович
 * @version 1.0
 */
public class ClientMain {

    /**
     * Логин юзера
     */
    private static String login;
    /**
     * Пароль юзера
     */
    private static String password;

    /**
     * Константа-адрес сервера
     */
    private static final String SERVER_HOST = "localhost";
    /**
     * Константа-номер порта сервера
     */
    private static final int SERVER_PORT = 12345;
    /**
     * Список команд, во время которых создаётся новый объект для коллекции
     */
    private static final List<String> COMMANDS_WITH_OBJECT = Arrays.asList(
            "insert", "update", "replace_if_greater", "remove_lower"
    );

    /**
     * Точка входа в программу.
     * Инициализирует терминал, считывает ввод, создаёт запрос и отправляет серверу.
     */
    public static void main(String[] args) {



        UDPClient updClient;

        try {
            //подключаем сетевое ядро
            updClient = new UDPClient(SERVER_HOST, SERVER_PORT);
        } catch (SocketException | UnknownHostException e) {
            System.err.println("\u001B[31mОшибка:\u001B[0m" + e.getMessage());
            return;
        }

        try {
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            StringsCompleter completer = new StringsCompleter(
                    "help", "info", "show", "insert", "update", "remove_key",
                    "clear", "execute_script", "exit", "history", "gavrilovsay",
                    "group_counting_by_minimal_point","print_field_descending_minimal_point",
                    "remove_lower", "replace_if_greater", "count_less_than_difficulty", "polyakov");

            LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).completer(completer).build();

            System.out.println("\u001B[34mАвторизация в систему\u001B[0m");

            //Введём логин
            while (true) {
                try {
                    login = lineReader.readLine("Введите логин: ").trim();
                    password = lineReader.readLine("Введите пароль: ", '*').trim();

                    if (login.isEmpty() || password.isEmpty()) {
                        System.out.println("\u001B[31mОшибка:\u001B[0m Логин и пароль не могут быть пустыми!");
                        continue;
                    }
                    break;
                } catch (UserInterruptException e) {
                    System.out.println("\u001B[33mНе ломайте программу бедного студента, пожалуйста\u001B[0m");
                    continue;
                } catch (EndOfFileException e) {
                    System.out.println("\nЗавершение сеанса...");
                    System.exit(0);
                }
            }

            LabWorkAsker asker = new LabWorkAsker(lineReader);

            ExecuteScriptCommand scriptExecutor = new ExecuteScriptCommand(updClient, asker, COMMANDS_WITH_OBJECT);



            while (true) {
                String input;
                try {
                    input = lineReader.readLine("\u001B[32mКонсольНоУжеКлиент@Что-то_заUмное2:~₽\u001B[0m ");
                } catch (EndOfFileException e) {
                    System.out.println("\nЗавершение работы программы...");
                    break;
                } catch (UserInterruptException e) {
                    System.out.println("\u001B[33mНе ломайте программу бедного студента, пожалуйста\u001B[0m");
                    continue;
                }

                if (input == null || input.trim().isEmpty()) continue;
                String[] tokens = input.trim().split("\\s+", 2);
                String commandName = tokens[0].toLowerCase();
                String argument;
                if (tokens.length > 1) {
                    argument = tokens[1];
                } else {
                    argument = "";
                }

                if (commandName.equals("exit")) {
                    try {
                        updClient.sendRequest(new Request("exit", "", null, login, password));
                    } catch (IOException e) {

                    }
                    System.out.println("Завершение работы...");
                    break;
                }
                if (commandName.equals("save")) {
                    System.out.println("\u001B[31mОшибка:\u001B[0m Команда 'save' доступна только на сервере.");
                    continue; // Пропускаем итерацию цикла, не отправляем запрос
                }
                if (commandName.equals("execute_script")) {
                    try {
                        updClient.sendRequest(new Request("execute_script", argument, null, login, password));
                        updClient.receiveResponse();

                        scriptExecutor.execute(argument, login, password);
                    } catch (IOException e) {
                        System.out.println("\u001B[31mОшибка:\u001B[0m связь неисправна при запуске скрипта");
                    }
                    continue;
                }

                if (commandName.equals("update")) {
                    // 1. Проверяем, что ID — число (локально)
                    if (argument.isEmpty()) {
                        System.out.println("Укажите ID."); continue;
                    }
                    try { Integer.parseInt(argument); }
                    catch (NumberFormatException e) { System.out.println("ID должен быть числом."); continue; }

                    // 2. ПРОВЕРКА НА СЕРВЕРЕ (Dry Run)
                    // Шлем запрос БЕЗ объекта LabWork
                    try {
                        updClient.sendRequest(new Request(commandName, argument, null, login, password));
                        Response checkResponse = updClient.receiveResponse();

                        if (!checkResponse.getSuccess()) {
                            System.err.println(checkResponse.getMessage());
                            continue; // Сервер сказал, что такого ID нет — не запускаем asker
                        }

                        // 3. Если сервер ответил успехом — значит ID существует!
                        System.out.println("Введите новые данные для объекта:");
                        LabWork updatedLab = asker.createLabWork();

                        // 4. Шлем финальный запрос уже С ОБЪЕКТОМ
                        updClient.sendRequest(new Request(commandName, argument, updatedLab, login, password));
                        Response finalResponse = updClient.receiveResponse();
                        System.out.println(finalResponse.getMessage());

                    } catch (IOException e) {
                        System.err.println("Ошибка связи: " + e.getMessage());
                    }
                    continue; // Переходим к следующему вводу
                }

                Request request;
                try {
                    if (COMMANDS_WITH_OBJECT.contains(commandName)) {
                        System.out.println("Команда '" + commandName + "' требует дополнительного ввода данных.");
                        LabWork labWork = asker.createLabWork();
                        request = new Request(commandName, argument, labWork, login, password);
                    } else {
                        request = new Request(commandName, argument, null, login, password);
                    }
                } catch (Exception e) {
                    System.out.println("\u001B[31mОшибка при формировании данных:\u001B[0m " + e.getMessage());
                    continue;
                }

                try {
                    // 1. Отправляем запрос
                    updClient.sendRequest(request);

                    // 2. Получаем ответ ОДИН РАЗ
                    Response response = updClient.receiveResponse();

                    // 3. Проверяем успех авторизации и выводим сообщение
                    if (response.getSuccess()) {
                        System.out.println(response.getMessage());
                    } else {
                        // Если сервер прислал ошибку авторизации
                        if (response.getMessage().contains("Ошибка авторизации") ||
                                response.getMessage().contains("неверный логин или пароль")) {

                            System.err.println("\u001B[31mКритическая ошибка:\u001B[0m " + response.getMessage());
                            System.err.println("Пожалуйста, перезапустите программу и введите корректные данные.");
                            System.exit(1);
                        } else {
                            // Обычная ошибка команды
                            System.out.println("\u001B[33mСервер:\u001B[0m " + response.getMessage());
                        }
                    }

                } catch (SocketTimeoutException e) {
                    System.out.println("\u001B[31mСетевая ошибка:\u001B[0m Сервер не ответил в течение 3 секунд.");
                    // Программа не упадет, а просто пойдет на новый виток while(true)
                } catch (PortUnreachableException e) {
                    System.out.println("\u001B[31mСетевая ошибка:\u001B[0m Порт сервера недоступен.");
                } catch (IOException e) {
                    System.out.println("\u001B[31mОшибка ввода/вывода:\u001B[0m " + e.getMessage());
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\u001B[31mОшибка инициализации интерфейса:\u001B[0m " + e.getMessage());
        } catch (IOException e) {
            System.err.println("\u001B[31mСистемная ошибка ввода-вывода (терминал не поддерживается):\u001B[0m " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\u001B[31mНепредвиденная ошибка клиента:\u001B[0m " + e.toString());
            e.printStackTrace();
        } finally {
            if (updClient != null) {
                updClient.close();
            }
        }
    }
}
