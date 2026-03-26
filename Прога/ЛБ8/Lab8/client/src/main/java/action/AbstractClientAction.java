package action;

import gui.MainFrame;
import i18n.ResourceManager;
import managers.UDPClient;
import network.Request;
import network.Response;

import javax.swing.*;
import java.net.SocketTimeoutException;

public abstract class AbstractClientAction implements ClientAction {
    protected final MainFrame mainFrame;
    protected final UDPClient udpClient;
    protected final String login;
    protected final String password;
    protected final ResourceManager i18n;

    public AbstractClientAction(MainFrame mainFrame, UDPClient udpClient, String login, String password) {
        this.mainFrame = mainFrame;
        this.udpClient = udpClient;
        this.login = login;
        this.password = password;
        this.i18n = ResourceManager.getInstance();
    }

    /**
     * Универсальный метод для отправки запроса в фоне и обновления UI
     */
    protected void executeNetworkTask(Request request, boolean showSuccessPopup) {
        new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                udpClient.sendRequest(request);
                return udpClient.receiveResponse();
            }

            @Override
            protected void done() {
                try {
                    Response resp = get();

                    String translatedMessage;
                    String messageKey = resp.getMessage();

                    if (resp.getMessageArgs() != null && resp.getMessageArgs().length > 0) {
                        translatedMessage = i18n.getFormatted(messageKey, resp.getMessageArgs());
                    } else {
                        translatedMessage = i18n.getString(messageKey);
                    }

                    if (translatedMessage.startsWith("!")) {
                        translatedMessage = messageKey;
                    }

                    if (resp.getSuccess()) {
                        if (showSuccessPopup) {
                            JOptionPane.showMessageDialog(mainFrame, translatedMessage,
                                    i18n.getString("info.title"), JOptionPane.INFORMATION_MESSAGE);
                        }
                        if (resp.getCollection() != null) {
                            mainFrame.updateTableData(resp.getCollection());
                        }
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, translatedMessage,
                                i18n.getString("error.title"), JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    String errorKey;
                    Object[] args = null;

                    Throwable cause = ex.getCause();

                    if (cause instanceof java.net.SocketTimeoutException) {
                        errorKey = "err.network.timeout";
                    } else if (cause instanceof java.net.PortUnreachableException) {
                        errorKey = "err.network.unreachable";
                    } else {
                        errorKey = "err.network.unknown";
                        args = new Object[]{cause != null ? cause.getMessage() : ex.getMessage()};
                    }

                    String translatedError = (args != null) ? i18n.getFormatted(errorKey, args) : i18n.getString(errorKey);

                    JOptionPane.showMessageDialog(mainFrame,
                            translatedError,
                            i18n.getString("error.title"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Выводит окно с ошибкой
     */
    protected void showError(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, i18n.getString("error.title"), JOptionPane.ERROR_MESSAGE);
    }

    protected String translateServerMessage(Response resp) {
        if (resp == null || resp.getMessage() == null) return "";

        String key = resp.getMessage();
        Object[] args = resp.getMessageArgs();

        // Если у сообщения есть аргументы (например, ID или число), используем форматирование
        if (args != null && args.length > 0) {
            return i18n.getFormatted(key, args);
        }

        // Если аргументов нет, просто возвращаем перевод ключа
        return i18n.getString(key);
    }

}
