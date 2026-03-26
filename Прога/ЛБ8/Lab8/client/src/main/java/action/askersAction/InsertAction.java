package action.askersAction;

import action.AbstractClientAction;
import gui.MainFrame;
import managers.UDPClient;
import models.LabWork;
import network.Request;

import java.awt.event.ActionEvent;

public class InsertAction extends AbstractClientAction {
    public InsertAction(MainFrame mainFrame, UDPClient udpClient, String login, String password) {
        super(mainFrame, udpClient, login, password);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LabWorkFormDialog dialog = new LabWorkFormDialog(mainFrame, i18n.getString("form.title.insert"), null);
        dialog.setVisible(true);

        LabWork newLab = dialog.getResult();
        if (newLab != null) {
            Request request = new Request("insert", "", newLab, login, password);
            executeNetworkTask(request, true);
        }
    }
}
