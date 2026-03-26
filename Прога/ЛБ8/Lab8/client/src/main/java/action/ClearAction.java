package action;

import gui.MainFrame;
import managers.UDPClient;
import network.Request;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ClearAction extends AbstractClientAction{

    public ClearAction(MainFrame mainFrame, UDPClient udpClient, String login, String password) {
        super(mainFrame, udpClient, login, password);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int choice = JOptionPane.showConfirmDialog(
                mainFrame,
                i18n.getString("dialog.clear.confirm"),
                i18n.getString("dialog.clear.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            Request request = new Request("clear", "", null, login, password);
            executeNetworkTask(request, true);
        }
    }
}
