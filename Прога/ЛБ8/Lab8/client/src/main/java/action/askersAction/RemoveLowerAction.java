package action.askersAction;

import action.AbstractClientAction;
import gui.MainFrame;
import managers.UDPClient;
import models.LabWork;
import network.Request;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Действие для команды "Удалить меньшие" (remove_lower).
 * Открывает диалог для создания объекта-эталона и отправляет его на сервер.
 */
public class RemoveLowerAction extends AbstractClientAction {

    /**
     * Конструктор действия.
     * @param mainFrame главное окно приложения.
     * @param udpClient сетевой клиент.
     * @param login логин пользователя.
     * @param password пароль пользователя.
     */
    public RemoveLowerAction(MainFrame mainFrame, UDPClient udpClient, String login, String password) {
        super(mainFrame, udpClient, login, password);
    }

    /**
     * Выполняется при нажатии на кнопку "Удалить меньшие".
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // 1. Предупреждаем пользователя о том, что сейчас произойдет (Хороший тон UI)
        JOptionPane.showMessageDialog(
                mainFrame,
                i18n.getString("dialog.remove_lower.info"),
                i18n.getString("info.title"),
                JOptionPane.INFORMATION_MESSAGE
        );

        LabWorkFormDialog dialog = new LabWorkFormDialog(mainFrame, i18n.getString("form.title.remove_lower"), null);
        dialog.setVisible(true);

        LabWork referenceLab = dialog.getResult();

        if (referenceLab == null) {
            return;
        }
        Request req = new Request("remove_lower", "", referenceLab, login, password);
        executeNetworkTask(req, true);
    }
}