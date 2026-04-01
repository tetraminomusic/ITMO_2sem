package action.executeScriptAction;

import action.AbstractClientAction;
import gui.MainFrame;
import managers.UDPClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class ExecuteScriptAction extends AbstractClientAction {
    private final ExecuteScriptCommand scriptExecutor;

    public ExecuteScriptAction(MainFrame mainFrame, UDPClient udpClient, String login, String password,
                               ExecuteScriptCommand scriptExecutor) {
        super(mainFrame, udpClient, login, password);
        this.scriptExecutor = scriptExecutor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //открытие системного проводника
        JFileChooser fileChooser = new JFileChooser(new File("."));
        fileChooser.setDialogTitle(i18n.getString("cmd.execute_script"));

        javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter(
                "Текстовые файлы (*.txt)", "txt"
        );

        fileChooser.setFileFilter(filter);

        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            //достаём файл
            File file = fileChooser.getSelectedFile();
            //создаём окно для скрипта
            ScriptLogDialog logDialog = new ScriptLogDialog(mainFrame);
            logDialog.setTitle(i18n.getString("window.script.title"));
            logDialog.setVisible(true);

            new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() {
                    publish(i18n.getFormatted("script.log.start", file.getName()));

                    // Вызываем исполнителя. Лямбда (cmd, resp) будет вызываться для каждой команды.
                    scriptExecutor.execute(file.getAbsolutePath(), login, password, (cmd, resp) -> {
                        if (resp == null) {
                            // Начало выполнения: переводим имя команды
                            String cmdName = i18n.getString("cmd." + cmd);
                            if (cmdName.startsWith("!")) cmdName = cmd; // если нет в свойствах
                            publish(i18n.getFormatted("script.log.executing", cmdName));
                        } else {
                            // Результат получен: переводим ответ сервера (ключ + аргументы)
                            String resultText;
                            if (resp.getMessageArgs() != null && resp.getMessageArgs().length > 0) {
                                resultText = i18n.getFormatted(resp.getMessage(), resp.getMessageArgs());
                            } else {
                                resultText = i18n.getString(resp.getMessage());
                            }
                            // Если это не ключ, а готовый текст (например, info), оставляем как есть
                            if (resultText.startsWith("!")) resultText = resp.getMessage();

                            // процесс отправляет переведённую строку в process
                            publish(i18n.getFormatted("script.log.result", resultText));
                        }
                    });

                    publish(i18n.getString("script.log.done"));
                    return null;
                }

                @Override
                protected void process(java.util.List<String> chunks) {
                    //дописывает новые строки в окно
                    for (String msg : chunks) logDialog.appendLog(msg);
                }

                @Override
                protected void done() {
                    mainFrame.loadInitialCollection(); // Обновляем таблицу в конце
                }
            }.execute();
        }
    }
}