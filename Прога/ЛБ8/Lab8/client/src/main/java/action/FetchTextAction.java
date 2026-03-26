package action;

import gui.MainFrame;
import managers.UDPClient;
import network.Request;
import network.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FetchTextAction extends AbstractClientAction {
    private final String commandName;
    private final String windowTitleKey;

    public FetchTextAction(MainFrame mainFrame, UDPClient udpClient, String login, String password,
                           String commandName, String windowTitleKey) {
        super(mainFrame, udpClient, login, password);
        this.commandName = commandName;
        this.windowTitleKey = windowTitleKey;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                udpClient.sendRequest(new Request(commandName, "", null, login, password));
                return udpClient.receiveResponse();
            }

            @Override
            protected void done() {
                try {
                    Response resp = get();
                    if (resp.getSuccess()) {
                        String translatedText;

                        if (resp.getMessageArgs() != null && resp.getMessageArgs().length > 0) {
                            translatedText = i18n.getFormatted(resp.getMessage(), resp.getMessageArgs());
                        } else {
                            translatedText = i18n.getString(resp.getMessage());
                        }

                        if (commandName.equals("history")) {
                            translatedText = translateHistoryString(translatedText);
                        }

                        showScrollableInfo(i18n.getString(windowTitleKey), translatedText);

                        if (resp.getCollection() != null) mainFrame.updateTableData(resp.getCollection());

                    } else {
                        showError(i18n.getString(resp.getMessage()));
                    }
                } catch (Exception ex) {
                    showError(i18n.getFormatted("err.network.unknown", ex.getMessage()));
                }
            }
        }.execute();
    }

    private void showScrollableInfo(String title, String content) {
        JTextArea ta = new JTextArea(15, 40);
        ta.setText(content);
        ta.setEditable(false);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JOptionPane.showMessageDialog(mainFrame, new JScrollPane(ta), title, JOptionPane.INFORMATION_MESSAGE);
    }

    private String translateHistoryString(String raw) {
        StringBuilder sb = new StringBuilder(i18n.getString("history.title")).append("\n");
        for (String line : raw.split("\n")) {
            if (line.trim().startsWith("-")) {
                String name = line.replace("-", "").trim();
                String trans = i18n.getString("cmd." + name);
                sb.append("- ").append(trans.startsWith("!") ? name : trans).append("\n");
            }
        }
        return sb.toString();
    }
}