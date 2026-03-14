import managers.UDPClient;
import managers.LabWorkAsker;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * Точка входа в клиентское приложение.
 */
public class ClientMain {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final List<String> COMMANDS_WITH_OBJECT = Arrays.asList(
            "insert", "update", "replace_if_greater", "remove_lower"
    );

    public static void main(String[] args) {
        UDPClient udpClient = null;

        try {
            udpClient = new UDPClient(SERVER_HOST, SERVER_PORT);

            Terminal terminal = TerminalBuilder.builder().system(true).build();
            StringsCompleter completer = new StringsCompleter(
                    "help", "info", "show", "insert", "update", "remove_key",
                    "clear", "execute_script", "exit", "history", "gavrilovsay",
                    "group_counting_by_minimal_point", "print_field_descending_minimal_point",
                    "remove_lower", "replace_if_greater", "count_less_than_difficulty", "polyakov"
            );

            LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).completer(completer).build();
            LabWorkAsker asker = new LabWorkAsker(lineReader);
            ExecuteScriptCommand scriptExecutor = new ExecuteScriptCommand(udpClient, asker, COMMANDS_WITH_OBJECT);

            // Создаем приложение и запускаем его
            ClientApp app = new ClientApp(udpClient, lineReader, asker, scriptExecutor, COMMANDS_WITH_OBJECT);
            app.start();

        } catch (SocketException | UnknownHostException e) {
            System.err.println("\u001B[31mОшибка сети:\u001B[0m " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("\u001B[31mОшибка инициализации интерфейса:\u001B[0m " + e.getMessage());
        } catch (IOException e) {
            System.err.println("\u001B[31mСистемная ошибка ввода-вывода:\u001B[0m " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\u001B[31mНепредвиденная ошибка клиента:\u001B[0m " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (udpClient != null) {
                udpClient.close();
            }
        }
    }
}