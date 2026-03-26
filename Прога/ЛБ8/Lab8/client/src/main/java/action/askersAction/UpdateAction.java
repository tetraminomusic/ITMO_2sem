package action.askersAction;

import action.AbstractClientAction;
import gui.MainFrame;
import managers.UDPClient;
import models.LabWork;
import network.Request;
import network.Response;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Действие для обновления объекта.
 * Сначала проверяет права владения на сервере, и только потом открывает форму.
 */
public class UpdateAction extends AbstractClientAction {

    public UpdateAction(MainFrame mainFrame, UDPClient udpClient, String login, String password) {
        super(mainFrame, udpClient, login, password);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String idString = mainFrame.getSelectedId();

        // 1. Проверяем, выбрана ли строка
        if (idString == null) {
            showError(i18n.getString("dialog.error.string.choose.table"));
            return;
        }

        // 2. ПЕРВЫЙ ЭТАП: Запрос к серверу на проверку прав (Dry Run)
        new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                // Шлем ID, но в поле объекта передаем null
                Request checkReq = new Request("update", idString, null, login, password);
                udpClient.sendRequest(checkReq);
                return udpClient.receiveResponse();
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.getSuccess()) {
                        // СЕРВЕР ПОДТВЕРДИЛ ПРАВА: открываем форму
                        openEditForm(idString);
                    } else {
                        // СЕРВЕР ОТКАЗАЛ: переводим ключ ошибки (напр. "access_denied") и показываем
                        showError(translateServerMessage(response));
                    }
                } catch (Exception ex) {
                    showError(i18n.getFormatted("err.network.unknown", ex.getMessage()));
                }
            }
        }.execute();
    }

    /**
     * ВТОРОЙ ЭТАП: Открытие формы и финальная отправка данных.
     */
    private void openEditForm(String idString) {
        try {
            int id = Integer.parseInt(idString);
            LabWork oldLab = mainFrame.getLabWorkById(id);

            if (oldLab == null) {
                showError(i18n.getString("server.msg.not_found"));
                return;
            }

            // Открываем диалог с данными старого объекта
            LabWorkFormDialog dialog = new LabWorkFormDialog(mainFrame, i18n.getString("form.title.update"), oldLab);
            dialog.setVisible(true);

            // Получаем результат редактирования
            LabWork updatedLab = dialog.getResult();

            if (updatedLab != null) {
                // ФИНАЛЬНЫЙ ЗАПРОС: шлем объект на сервер для записи в БД
                executeNetworkTask(new Request("update", idString, updatedLab, login, password), true);
            }
        } catch (NumberFormatException ex) {
            showError(i18n.getString("err.validation.number.id"));
        }
    }
}