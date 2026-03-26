package action;

import gui.MainFrame;
import managers.UDPClient;
import network.Request;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RemoveAction extends AbstractClientAction{

    public RemoveAction(MainFrame mainFrame, UDPClient udpClient, String login, String password) {
        super(mainFrame, udpClient, login, password);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String id = mainFrame.getSelectedId();
        if (id == null) {
            showError(i18n.getString("dialog.error.string.choose.table"));
            return;
        }

        String confirmMessage = i18n.getFormatted("dialog.delete.confirm", id);

        int choice = JOptionPane.showConfirmDialog(
                mainFrame,
                confirmMessage,
                i18n.getString("dialog.confirm.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            executeNetworkTask(new Request("remove_key", id, null, login, password), true);
        }
    }
}
