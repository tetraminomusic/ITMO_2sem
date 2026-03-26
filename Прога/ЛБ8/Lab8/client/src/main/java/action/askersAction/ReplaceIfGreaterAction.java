package action.askersAction;

import action.AbstractClientAction;
import gui.MainFrame;
import managers.UDPClient;
import models.LabWork;
import network.Request;
import network.Response;
import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;

public class ReplaceIfGreaterAction extends AbstractClientAction {

    public ReplaceIfGreaterAction(MainFrame mainFrame, UDPClient udpClient, String login, String password) {
        super(mainFrame, udpClient, login, password);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String idString = mainFrame.getSelectedId();
        if (idString == null) {
            showError(i18n.getString("dialog.error.string.choose.table"));
            return;
        }

        // ШАГ 1: Проверка прав на сервере
        new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                // Шлем запрос на сервер без объекта
                Request checkReq = new Request("replace_if_greater", idString, null, login, password);
                udpClient.sendRequest(checkReq);
                return udpClient.receiveResponse();
            }

            @Override
            protected void done() {
                try {
                    Response resp = get();
                    if (resp.getSuccess()) {
                        // ШАГ 2: Права есть, открываем форму для нового объекта
                        openReplaceForm(idString);
                    } else {
                        showError(translateServerMessage(resp));
                    }
                } catch (Exception ex) {
                    showError("Сетевая ошибка: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void openReplaceForm(String idString) {
        LabWorkFormDialog dialog = new LabWorkFormDialog(mainFrame, i18n.getString("cmd.replace_greater"), null);
        dialog.setVisible(true);

        LabWork newLab = dialog.getResult();
        if (newLab != null) {
            // ШАГ 3: Финальная отправка на замену
            executeNetworkTask(new Request("replace_if_greater", idString, newLab, login, password), true);
        }
    }
}