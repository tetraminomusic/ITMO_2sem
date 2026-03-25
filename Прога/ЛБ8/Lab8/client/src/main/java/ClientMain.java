import gui.LoginFrame;
import i18n.ResourceManager;
import managers.UDPClient;
import managers.LabWorkAsker;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * Точка входа в клиентское приложение.
 */
public class ClientMain {
    private static String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final List<String> COMMANDS_WITH_OBJECT = Arrays.asList(
            "insert", "update", "replace_if_greater", "remove_lower"
    );

    public static void main(String[] args) {

        if (args.length > 0) {
            SERVER_HOST = args[0];
        }

        javax.swing.plaf.FontUIResource largeFont = new javax.swing.plaf.FontUIResource("Comic Sans MS", Font.PLAIN, 16);
        setUIFont(largeFont);

        try {
            ResourceManager.getInstance();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Критическая ошибка: не найдены файлы локализации (messages.properties).\n" + e.getMessage(),
                    "Ошибка ресурсов", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Инициализируем сетевой клиент для обмена данными
        UDPClient udpClient = null;
        try {
            udpClient = new UDPClient(SERVER_HOST, SERVER_PORT);
        } catch (SocketException | UnknownHostException e) {
            JOptionPane.showMessageDialog(null,
                    "Сетевая ошибка: не удалось инициализировать UDP клиент.\n" + e.getMessage(),
                    "Критическая ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- 2. Запуск GUI в потоке Swing (EDT) ---

        final UDPClient finalUdpClient = udpClient;

        // SwingUtilities.invokeLater гарантирует, что все операции с GUI
        // будут выполняться в специальном потоке, как того требует Swing.
        SwingUtilities.invokeLater(() -> {
            try {
                // Устанавливаем системный Look & Feel для более "родного" вида окон
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Создаем и показываем первое окно — окно авторизации
                LoginFrame loginFrame = new LoginFrame(finalUdpClient);
                loginFrame.setVisible(true);
            } catch (Exception e) {
                // Обработка критических ошибок при запуске GUI
                JOptionPane.showMessageDialog(null,
                        "Критическая ошибка при запуске интерфейса: " + e.getMessage(),
                        "Ошибка запуска", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }

    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }
}