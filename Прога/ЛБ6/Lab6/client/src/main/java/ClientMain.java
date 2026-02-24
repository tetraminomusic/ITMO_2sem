import managers.LabWorkAsker;

import models.LabWork;
import network.Request;
import network.Response;
import network.SerializationUtils;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
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

    private static final int PORT = 12345;
    private static final String HOST = "localhost";

    private static final List<String> OBJECT_COMMANDS = Arrays.asList("insert", "update", "remove_lower", "replace_if_greater");

    /**
     * Точка входа в программу.
     * <p>В методе происходит:</p>
     * <ul>
     *     <li>Настройка терминала и автодополнения (JLine).</li>
     *     <li>Загрузка коллекции из JSON-файла (путь берется из переменной окружения LAB_DATA_PATH).</li>
     *     <li>Запуск Read-Eval-Print Loop (REPL) для взаимодействия с пользователем.</li>
     * </ul>
     *
     * @param args аргументы командной строки (в данной программе не используются).
     */
    public static void main(String[] args) {
        System.out.println("Добро пожаловать в приложение LabWork!");
        try (DatagramSocket socket = new DatagramSocket()) {

            socket.setSoTimeout(3000);
            InetAddress address = InetAddress.getByName(HOST);

            Terminal terminal = TerminalBuilder.builder().system(true).build();
            StringsCompleter completer = new StringsCompleter(
                    "help", "info", "show", "insert", "update", "remove_key",
                    "clear", "save", "execute_script", "exit", "history", "gavrilovsay",
                    "group_counting_by_minimal","print_field_descending_minimal_point",
                    "remove_lover", "replace_if_greater", "count_less_than_difficulty", "polyakov", "write_ПСЖ");

            LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).completer(completer).build();

            LabWorkAsker asker = new LabWorkAsker(lineReader);


            //обработка ввода
            while (true) {
                String input;
                try {
                    input = lineReader.readLine("\u001B[32mКонсоль,ноУжеКлиент@Что-то_заUмное:~₽\u001B[0m ");
                } catch (EndOfFileException e) {
                    System.out.println("\nЗавершение работы клиента...");
                    break;
                } catch (UserInterruptException e) {
                    System.out.println("\u001B[33mНе ломайте программу бедного студента, пожалуйста\u001B[0m");
                    continue;
                }

                if (input == null || input.trim().isEmpty()) continue;

                String[] tokens = input.trim().split("\\s+", 2);
                String command = tokens[0].toLowerCase();
                String arg;
                if (tokens.length > 1) {
                    arg = tokens[1];
                } else {
                    arg = "";
                }

                if (command == "exit") {
                    System.out.println("Завершение работы клиента");
                    break;
                }


                Request request = null;

                //либо команда ориентирована на отправку лабы, либо она взаимодействует с данным на серваке
                try {
                    if (OBJECT_COMMANDS.contains(command)) {
                        LabWork lab = asker.createLabWork(0);
                        request = new Request(command, arg, lab);
                    } else {
                        request = new Request(command, arg, null);
                    }

                    byte[] sendData = SerializationUtils.serialize(request);

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, PORT);
                    socket.send(sendPacket);

                    byte[] receiveBuffer = new byte[65536];
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                    socket.receive(receivePacket);
                    Response response = (Response) SerializationUtils.deserialize(receivePacket.getData());

                    if (response.isSuccess()) {
                        System.out.println(response.getMessage());
                    } else {
                        System.out.println("Сервер " + response.getMessage());
                    }

                } catch (SocketTimeoutException e) {
                    System.out.println("\u001B[31mОшибка:\u001B[0m Сервер временно недоступен. Попробуйте позже.");
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("\u001B[31mОшибка сети:\u001B[0m " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Критическая \u001B[31mОшибка\u001B[0m: " + e.getMessage());
        }
    }
}
