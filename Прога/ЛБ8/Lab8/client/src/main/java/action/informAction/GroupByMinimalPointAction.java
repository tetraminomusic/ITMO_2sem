package action.informAction;

import action.AbstractClientAction;
import gui.MainFrame;
import managers.UDPClient;
import network.Request;
import network.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GroupByMinimalPointAction extends AbstractClientAction {

    public GroupByMinimalPointAction(MainFrame mainFrame, UDPClient udpClient, String login, String password) {
        super(mainFrame, udpClient, login, password);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Запускаем фоновую задачу
        new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                // Шлем запрос на сервер
                Request req = new Request("group_counting_by_minimal_point", "", null, login, password);
                udpClient.sendRequest(req);
                return udpClient.receiveResponse();
            }

            @Override
            protected void done() {
                try {
                    Response resp = get();

                    // --- ПЕРЕВОД СООБЩЕНИЯ СЕРВЕРА ---
                    // Используем метод translateServerMessage из нашего базового класса
                    // Он возьмет ключ "server.msg.group_success" и подставит туда данные
                    String translatedMessage = translateServerMessage(resp);

                    if (resp.getSuccess()) {
                        // Показываем результат в красивом окне со скроллом
                        showScrollableInfo(i18n.getString("window.group.title"), translatedMessage);

                        // Если коллекция изменилась, обновляем её в GUI
                        if (resp.getCollection() != null) {
                            mainFrame.updateTableData(resp.getCollection());
                        }
                    } else {
                        // Показываем ошибку (например, если коллекция пуста)
                        showError(translatedMessage);
                    }
                } catch (Exception ex) {
                    showError(i18n.getFormatted("err.network.unknown", ex.getMessage()));
                }
            }
        }.execute();
    }

    /**
     * Показывает текст во всплывающем окне с прокруткой.
     */
    private void showScrollableInfo(String title, String content) {
        JTextArea textArea = new JTextArea(15, 35);
        textArea.setText(content);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(mainFrame, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }
}