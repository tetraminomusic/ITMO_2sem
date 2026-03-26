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
 * Действие для выполнения команды вывода баллов по убыванию.
 */
public class PrintFieldDescendingAction extends AbstractClientAction {

    public PrintFieldDescendingAction(MainFrame mainFrame, UDPClient udpClient, String login, String password) {
        super(mainFrame, udpClient, login, password);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                // Шлем запрос на сервер
                Request req = new Request("print_field_descending_minimal_point", "", null, login, password);
                udpClient.sendRequest(req);
                return udpClient.receiveResponse();
            }

            @Override
            protected void done() {
                try {
                    Response resp = get();

                    // Переводим сообщение сервера через метод базового класса
                    // Он подставит список баллов в плейсхолдер {0}
                    String translatedMessage = translateServerMessage(resp);

                    if (resp.getSuccess()) {
                        // Показываем в окне со скроллом
                        showScrollableInfo(i18n.getString("window.print_desc.title"), translatedMessage);

                        // Синхронизируем таблицу
                        if (resp.getCollection() != null) {
                            mainFrame.updateTableData(resp.getCollection());
                        }
                    } else {
                        showError(translatedMessage);
                    }
                } catch (Exception ex) {
                    showError(i18n.getFormatted("err.network.unknown", ex.getMessage()));
                }
            }
        }.execute();
    }

    private void showScrollableInfo(String title, String content) {
        JTextArea textArea = new JTextArea(15, 25); // Это окно может быть поуже (только числа)
        textArea.setText(content);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(mainFrame, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }
}