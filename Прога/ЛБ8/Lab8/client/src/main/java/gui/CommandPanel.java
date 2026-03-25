package gui;

import java.util.List;
import i18n.LocaleChangeListener;
import i18n.ResourceManager;
import managers.UDPClient;
import models.LabWork;
import models.Person;
import network.Request;
import network.Response;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CommandPanel extends JPanel implements LocaleChangeListener {
    private final UDPClient udpClient;
    private final String login;
    private final String password;
    private final MainFrame mainFrame;
    private final ExecuteScriptCommand scriptExecutor;

    ResourceManager i18n = ResourceManager.getInstance();

    private final Frame parentFrame;

    private JButton btnInsert;
    private JButton btnUpdate;
    private JButton btnRemove;
    private JButton btnClear;
    private JButton btnExecuteScript;
    private JButton btnRemoveLower;
    private JButton btnHistory;
    private JButton btnReplaceIfGreater;
    private JButton btnGroupByMinimalPoint;
    private JButton btnCountLessThanDifficulty;
    private JButton btnPrintFieldDescendingMinimalPoint;

    public CommandPanel(UDPClient udpClient, String login, String password, MainFrame mainFrame) {
        this.udpClient = udpClient;
        this.login = login;
        this.password = password;
        this.mainFrame = mainFrame;
        this.parentFrame = mainFrame;

        List<String> objectCmds = java.util.Arrays.asList("insert", "update", "replace_if_greater", "remove_lower");
        this.scriptExecutor = new ExecuteScriptCommand(udpClient, new managers.LabWorkAsker(null), objectCmds);

        //выравниваем все элементы по вертикали
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //сделаем паддинг
        setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        //инициализация самих кнопок
        initComponents();

        //настраиваем локаль
        ResourceManager.getInstance().addLocaleChangeListener(this);

        //перевод текста в первый раз при создании панели
        onLocaleChange();
    }

    private void initComponents() {
        // Инициализация самих кнопок
        btnInsert = new JButton();
        btnUpdate = new JButton();
        btnRemove = new JButton();
        btnClear = new JButton();
        btnRemoveLower = new JButton();
        btnReplaceIfGreater = new JButton();
        btnExecuteScript = new JButton();
        btnHistory = new JButton();
        btnGroupByMinimalPoint = new JButton();
        btnCountLessThanDifficulty = new JButton();
        btnPrintFieldDescendingMinimalPoint = new JButton();

        // Выравнивание по центру кнопок
        styleButton(btnInsert);
        styleButton(btnUpdate);
        styleButton(btnRemove);
        styleButton(btnClear);
        styleButton(btnRemoveLower);
        styleButton(btnReplaceIfGreater);
        styleButton(btnExecuteScript);
        styleButton(btnHistory);
        styleButton(btnGroupByMinimalPoint);
        styleButton(btnCountLessThanDifficulty);
        styleButton(btnPrintFieldDescendingMinimalPoint);

        // Добавление на панель с отступами
        // Базовые операции (CRUD)

        // Операция Insert
        btnInsert.addActionListener(e -> {
            //создаём окно
            LabWorkFormDialog dialog = new LabWorkFormDialog(parentFrame, i18n.getString("form.title.insert"), null);
            dialog.setVisible(true);
            //блокирует окно
            LabWork newLab = dialog.getResult();

            if (newLab != null) {
                new SwingWorker<Response, Void>() {
                    @Override
                    protected Response doInBackground() throws Exception {
                        //создаём запрос с новым объектом
                        Request rq = new Request("insert", "", newLab, login, password);
                        udpClient.sendRequest(rq);
                        return udpClient.receiveResponse();
                    }

                    @Override
                    protected void done() {
                        try {
                            Response resp = get();
                            if (resp.getSuccess()) {
                                //если успешно добавилось - то просим обновить окно об обновлении таблицы
                                JOptionPane.showMessageDialog(mainFrame, resp.getMessage());
                                mainFrame.loadInitialCollection();
                            } else {
                                JOptionPane.showMessageDialog(mainFrame, "Ошибка: " + resp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(mainFrame, "Ошибка сети: " + ex.getMessage());
                        }
                    }
                }.execute();
            }
        });
        add(btnInsert);


        //HISTORY
        btnHistory.addActionListener(e -> {
            String title = ResourceManager.getInstance().getString("cmd.history");
            fetchAndShowHistory(title);
        });




        //GROUP

        btnGroupByMinimalPoint.addActionListener(e -> fetchAndShowText("group_counting_by_minimal_point", i18n.getString("stat.group_min")));



        //SCRIPT

        btnExecuteScript.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(i18n.getString("cmd.execute_script"));

            int result = fileChooser.showOpenDialog(mainFrame);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                ScriptLogDialog logDialog = new ScriptLogDialog(mainFrame);
                logDialog.setVisible(true);

                new SwingWorker<Void, String>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        publish("Запуск скрипта " + selectedFile.getName() + ":");
                        scriptExecutor.execute(selectedFile.getAbsolutePath(), login, password, message -> {
                            publish(message);
                        });

                        publish("Завершено");
                        return null;
                    }

                    @Override
                    protected void process(java.util.List<String> chunks) {
                        // Этот метод вызывается в потоке Swing и безопасно пишет в окно
                        for (String msg : chunks) {
                            logDialog.appendLog(msg);
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            mainFrame.loadInitialCollection(); // Обновляем таблицу после скрипта
                        } catch (Exception ex) {
                            logDialog.appendLog("ОШИБКА: " + ex.getMessage());
                        }
                    }
                }.execute();
            }
        });



        //CLEAR
        btnClear.addActionListener(e -> {
            ResourceManager i18n = ResourceManager.getInstance();

            int choice = JOptionPane.showConfirmDialog(
                    mainFrame,
                    i18n.getString("dialog.clear.confirm"),
                    i18n.getString("dialog.clear.title"),
                    JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                new SwingWorker<Response, Void>() {
                    @Override
                    protected Response doInBackground() throws Exception {
                        Request req = new Request("clear", "", null, login, password);
                        udpClient.sendRequest(req);
                        return udpClient.receiveResponse();
                    }

                    @Override
                    protected void done() {
                        try {
                            Response resp = get();
                            if (resp.getSuccess()) {
                                //показываем сообщение об успехе
                                JOptionPane.showMessageDialog(mainFrame, resp.getMessage());

                                //синхронизируем таблицу

                                if (resp.getCollection() != null) {
                                    mainFrame.updateTableData(resp.getCollection());
                                }
                            } else {
                                JOptionPane.showMessageDialog(mainFrame, "Ошибка: " + resp.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(mainFrame, "Ошибка связи: " + ex.getMessage());
                        }
                    }
                }.execute();
            }
        });




        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnUpdate); // Добавили кнопку Update
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnRemove);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnClear);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Сложные операции изменения
        add(btnRemoveLower);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnReplaceIfGreater);
        add(Box.createRigidArea(new Dimension(0, 20))); // Отделим статистику бОльшим отступом

        // Операции статистики
        add(btnHistory);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnCountLessThanDifficulty);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnGroupByMinimalPoint);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnPrintFieldDescendingMinimalPoint);
        add(Box.createRigidArea(new Dimension(0, 30))); // Отделим системные команды

        // Системные команды
        add(btnExecuteScript);
    }

    @Override
    public void onLocaleChange() {
        btnInsert.setText(i18n.getString("cmd.insert"));
        btnUpdate.setText(i18n.getString("cmd.update"));
        btnRemove.setText(i18n.getString("cmd.remove_key"));
        btnClear.setText(i18n.getString("cmd.clear"));
        btnRemoveLower.setText(i18n.getString("cmd.remove_lower"));
        btnReplaceIfGreater.setText(i18n.getString("cmd.replace_greater"));
        btnExecuteScript.setText(i18n.getString("cmd.execute_script"));
        btnHistory.setText(i18n.getString("cmd.history"));
        btnGroupByMinimalPoint.setText(i18n.getString("cmd.group_by_minimal_point"));
        btnPrintFieldDescendingMinimalPoint.setText(i18n.getString("cmd.print_field_descending_minimal_point"));
        btnCountLessThanDifficulty.setText(i18n.getString("cmd.count_less_than_difficulty"));
    }

    private void styleButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));

    }

    private void showScrollableInfo(String title, String content) {
        JTextArea textArea = new JTextArea(15,40);
        textArea.setText(content);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(mainFrame, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void fetchAndShowText(String cmdName, String windowTitle) {
        new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                // Отправляем запрос на сервер
                Request req = new Request(cmdName, "", null, login, password);
                udpClient.sendRequest(req);
                return udpClient.receiveResponse();
            }

            @Override
            protected void done() {
                try {
                    Response resp = get();
                    if (resp.getSuccess()) {
                        showScrollableInfo(windowTitle, resp.getMessage());
                        if (resp.getCollection() != null) mainFrame.updateTableData(resp.getCollection());
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "Ошибка: " + resp.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainFrame, "Ошибка связи: " + ex.getMessage());
                }
            }
        }.execute();
    }

    /**
     * Запрашивает историю команд у сервера, переводит названия команд и выводит их.
     */
    private void fetchAndShowHistory(String windowTitle) {
        new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                // Шлем запрос на сервер
                Request req = new Request("history", "", null, login, password);
                udpClient.sendRequest(req);
                return udpClient.receiveResponse();
            }

            @Override
            protected void done() {
                try {
                    Response resp = get();
                    if (resp.getSuccess()) {
                        // 1. Получаем сырой текст от сервера
                        String rawText = resp.getMessage();

                        // 2. Переводим технические имена команд в красивые русские/английские имена
                        String translatedText = translateHistoryString(rawText);

                        // 3. Выводим в красивом окне со скроллом
                        showScrollableInfo(windowTitle, translatedText);

                    } else {
                        JOptionPane.showMessageDialog(mainFrame, resp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainFrame, "Ошибка связи: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private String translateHistoryString(String raw) {
        ResourceManager i18n = ResourceManager.getInstance();
        StringBuilder sb = new StringBuilder();

        // Разбиваем текст по строкам
        String[] lines = raw.split("\n");

        for (String line : lines) {
            // Если строка начинается с "- " (это команда)
            if (line.trim().startsWith("-")) {
                // Отрезаем тире и пробелы, получаем имя (например, "insert")
                String techName = line.replace("-", "").trim();

                // Ищем перевод по ключу cmd.techName
                String translatedName = i18n.getString("cmd." + techName);

                // Если перевод не найден, оставляем как есть
                if (translatedName.startsWith("!")) {
                    translatedName = techName;
                }

                sb.append("- ").append(translatedName).append("\n");
            } else {
                // Если это заголовок "Список последних команд:", тоже пытаемся перевести
                // Добавь в properties ключ: history.title=Список последних команд:
                sb.append(i18n.getString("history.title")).append("\n");
            }
        }
        return sb.toString();
    }
}
