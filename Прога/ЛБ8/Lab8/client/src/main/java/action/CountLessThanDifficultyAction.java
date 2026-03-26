package action;

import gui.MainFrame;
import managers.UDPClient;
import models.Difficulty;
import network.Request;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CountLessThanDifficultyAction extends AbstractClientAction {

    public CountLessThanDifficultyAction(MainFrame mainFrame, UDPClient udpClient, String login, String password) {
        super(mainFrame, udpClient, login, password);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox<Difficulty> diffBox = new JComboBox<>(Difficulty.values());

        int result = JOptionPane.showConfirmDialog(mainFrame, diffBox,
                i18n.getString("stat.count_diff"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Difficulty selected = (Difficulty) diffBox.getSelectedItem();
            if (selected != null) {
                executeNetworkTask(new Request("count_less_difficulty", selected.name(), null, login, password), true);
            }
        }
    }
}