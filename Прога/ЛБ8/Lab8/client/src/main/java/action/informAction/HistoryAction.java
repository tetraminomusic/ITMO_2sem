package action.informAction;

import action.AbstractClientAction;
import gui.MainFrame;
import managers.UDPClient;
import network.Request;
import network.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Действие для вывода истории команд.
 * Выполняет перевод технических имен команд на язык пользователя.
 */
public class HistoryAction extends AbstractClientAction {

    public HistoryAction(MainFrame mainFrame, UDPClient udpClient, String login, String password) {
        super(mainFrame, udpClient, login, password);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                udpClient.sendRequest(new Request("history", "", null, login, password));
                return udpClient.receiveResponse();
            }

            @Override
            protected void done() {
                try {
                    Response resp = get();
                    if (resp.getSuccess()) {
                        // 1. Сначала переводим "сообщение-ключ" от сервера (через метод в AbstractClientAction)
                        // Это превратит ключ в строку: "Список последних команд:\n - show\n - insert..."
                        String formattedHistory = translateServerMessage(resp);

                        // 2. Теперь переводим технические имена (show -> Показать)
                        String fullyTranslated = translateHistoryString(formattedHistory);

                        // 3. Показываем результат
                        showScrollableInfo(i18n.getString("window.history.title"), fullyTranslated);

                        if (resp.getCollection() != null) mainFrame.updateTableData(resp.getCollection());
                    } else {
                        // Если ошибка (например, история пуста или ошибка авторизации)
                        showError(translateServerMessage(resp));
                    }
                } catch (Exception ex) {
                    showError(i18n.getFormatted("err.network.unknown", ex.getMessage()));
                }
            }
        }.execute();
    }

    /**
     * Показывает текст в отдельном окне с прокруткой.
     */
    private void showScrollableInfo(String title, String content) {
        JTextArea ta = new JTextArea(15, 40);
        ta.setText(content);
        ta.setEditable(false);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JOptionPane.showMessageDialog(mainFrame, new JScrollPane(ta), title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Разбирает строку со списком команд и переводит каждую из них.
     */
    private String translateHistoryString(String raw) {
        StringBuilder sb = new StringBuilder();

        String[] lines = raw.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("-")) {
                // Извлекаем "show" из "- show"
                String techName = trimmed.substring(1).trim();
                // Пытаемся найти перевод cmd.show
                String trans = i18n.getString("cmd." + techName);
                sb.append("- ").append(trans.startsWith("!") ? techName : trans).append("\n");
            } else {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}