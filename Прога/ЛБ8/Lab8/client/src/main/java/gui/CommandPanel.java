package gui;

import java.util.List;
import i18n.LocaleChangeListener;
import i18n.ResourceManager;
import managers.UDPClient;
import models.Difficulty;
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



        //UPDATE

        btnUpdate.addActionListener(e -> {
            ResourceManager i18n = ResourceManager.getInstance();

            String idStr = mainFrame.getSelectedId();

            if (idStr == null) {
                JOptionPane.showMessageDialog(mainFrame, "Сначала выберете объект в таблице для редактирования!", i18n.getString("info.title"), JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                int id = Integer.parseInt(idStr);

                LabWork oldLab = mainFrame.getLabWorkById(id);

                if (oldLab == null) {
                    JOptionPane.showMessageDialog(mainFrame, "Объект не найден в локальной памяти");
                    return;
                }

                //открываем форму

                LabWorkFormDialog dialog = new LabWorkFormDialog(parentFrame, i18n.getString("form.title.update"), oldLab);
                dialog.setVisible(true);

                LabWork updatedLab = dialog.getResult();

                if (updatedLab != null) {
                    new SwingWorker<Response, Void>() {
                        @Override
                        protected Response doInBackground() throws Exception {
                            Request req = new Request("update", String.valueOf(id), updatedLab, login, password);
                            udpClient.sendRequest(req);
                            return udpClient.receiveResponse();
                        }

                        @Override
                        protected void done() {
                            try {
                                Response resp = get();
                                if (resp.getSuccess()) {
                                    //если сервер дал добро на обновление, то делаем
                                    JOptionPane.showMessageDialog(mainFrame, resp.getMessage());
                                    if (resp.getCollection() != null) {
                                        mainFrame.updateTableData(resp.getCollection());
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(mainFrame, resp.getMessage(), i18n.getString("error.title"), JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(mainFrame, "Ошибка сети: " + e.getMessage());
                            }
                        }
                    }.execute();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame,  "Ошибка чтения ID строки");
            }

        });


        //DELETE

        btnRemove.addActionListener(e -> {
            ResourceManager i18n = ResourceManager.getInstance();

            String selectedId = mainFrame.getSelectedId();

            if (selectedId == null || selectedId.isEmpty()) {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        i18n.getString("err.validation.empty"),
                        i18n.getString("info.title"),
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            //спрашиваем подтверждение удаления

            String confirmMessage = i18n.getFormatted("dialog.delete.condifm", selectedId);

            int choice = JOptionPane.showConfirmDialog(
                    mainFrame,
                    confirmMessage,
                    i18n.getString("dialog.confirm.title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                new SwingWorker<Response, Void>(){
                    @Override
                    protected Response doInBackground() throws Exception {
                        Request request = new Request("remove_key", selectedId, null, login, password);
                        udpClient.sendRequest(request);
                        return udpClient.receiveResponse();
                    }

                    @Override
                    protected void done() {
                        try {
                            Response response = get();
                            if (response.getSuccess()) {
                                JOptionPane.showMessageDialog(
                                        mainFrame,
                                        response.getMessage(),
                                        i18n.getString("info.title"),
                                        JOptionPane.INFORMATION_MESSAGE
                                );
                                if (response.getCollection() != null) {
                                    mainFrame.updateTableData(response.getCollection());
                                }
                            } else {
                                JOptionPane.showMessageDialog(
                                        mainFrame,
                                        response.getMessage(),
                                        i18n.getString("error.title"),
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(
                                    mainFrame,
                                    "Ошибка сети: " + e.getMessage(),
                                    i18n.getString("error.title"),
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                }.execute();
            }
        });


        //REMOVE LOWER

        btnRemoveLower.addActionListener(e -> {
            ResourceManager i18n = ResourceManager.getInstance();

            // 1. Открываем окно ввода для создания "эталона"
            // Используем тот же диалог, что и для insert, но с другим заголовком
            LabWorkFormDialog dialog = new LabWorkFormDialog(parentFrame, i18n.getString("form.title.remove_lower"), null);
            dialog.setVisible(true); // Программа ждет, пока пользователь не закроет диалог

            // 2. Получаем созданный объект (эталон для сравнения)
            LabWork referenceLab = dialog.getResult();

            // 3. Если пользователь нажал "Отмена" или просто закрыл окно
            if (referenceLab == null) {
                return;
            }

            // 4. Если объект создан, отправляем его на сервер для массового удаления
            new SwingWorker<Response, Void>() {
                @Override
                protected Response doInBackground() throws Exception {
                    // Формируем запрос. В argument передаем пустоту, в objectArgument - наш эталон
                    Request req = new Request("remove_lower", "", referenceLab, login, password);
                    udpClient.sendRequest(req);
                    return udpClient.receiveResponse();
                }

                @Override
                protected void done() {
                    try {
                        Response resp = get();
                        if (resp.getSuccess()) {
                            // Если удаление прошло успешно (сервер вернет количество удаленных)
                            JOptionPane.showMessageDialog(
                                    mainFrame,
                                    resp.getMessage(),
                                    i18n.getString("info.title"),
                                    JOptionPane.INFORMATION_MESSAGE
                            );

                            // Обновляем таблицу (удаленные объекты исчезнут)
                            if (resp.getCollection() != null) {
                                mainFrame.updateTableData(resp.getCollection());
                            }
                        } else {
                            // Если произошла ошибка на сервере
                            JOptionPane.showMessageDialog(
                                    mainFrame,
                                    resp.getMessage(),
                                    i18n.getString("error.title"),
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(mainFrame, "Ошибка сети: " + ex.getMessage());
                    }
                }
            }.execute();
        });



        // Replace if Greater

        btnReplaceIfGreater.addActionListener(e -> {
            ResourceManager i18n = ResourceManager.getInstance();

            // 1. Узнаем ID выбранной строки из таблицы
            String selectedId = mainFrame.getSelectedId();

            if (selectedId == null || selectedId.isEmpty()) {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "Сначала выберите объект в таблице для замены!",
                        i18n.getString("info.title"),
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                int id = Integer.parseInt(selectedId);

                // Получаем старый объект из памяти для информативности (опционально)
                LabWork oldLab = mainFrame.getLabWorkById(id);
                if (oldLab == null) {
                    JOptionPane.showMessageDialog(mainFrame, "Объект не найден в локальной памяти.");
                    return;
                }

                // 2. Открываем окно для ввода данных "Претендента"
                // Передаем null, так как мы создаем НОВЫЙ объект с нуля, а не редактируем старый
                LabWorkFormDialog dialog = new LabWorkFormDialog(parentFrame, "Создание претендента на замену", null);
                dialog.setVisible(true); // Программа ждет закрытия окна

                // 3. Получаем созданный объект
                LabWork newLab = dialog.getResult();

                // 4. Если пользователь нажал "Отмена"
                if (newLab == null) {
                    return;
                }

                // 5. Отправляем запрос на сервер в фоновом потоке
                new SwingWorker<Response, Void>() {
                    @Override
                    protected Response doInBackground() throws Exception {
                        // В argument передаем ID старого объекта, в objectArgument - нового претендента
                        Request req = new Request("replace_if_greater", selectedId, newLab, login, password);
                        udpClient.sendRequest(req);
                        return udpClient.receiveResponse();
                    }

                    @Override
                    protected void done() {
                        try {
                            Response resp = get();
                            if (resp.getSuccess()) {
                                // Показываем сообщение ("Замена произведена")
                                JOptionPane.showMessageDialog(
                                        mainFrame,
                                        resp.getMessage(),
                                        i18n.getString("info.title"),
                                        JOptionPane.INFORMATION_MESSAGE
                                );

                                // Обновляем таблицу (старый объект исчезнет, появится новый)
                                if (resp.getCollection() != null) {
                                    mainFrame.updateTableData(resp.getCollection());
                                }
                            } else {
                                // Если сервер отказал (чужой объект или новый оказался меньше)
                                JOptionPane.showMessageDialog(
                                        mainFrame,
                                        resp.getMessage(),
                                        i18n.getString("error.title"),
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(mainFrame, "Ошибка сети: " + ex.getMessage());
                        }
                    }
                }.execute();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Ошибка чтения ID строки.");
            }
        });

        // --- КНОПКА "КОЛ-ВО < СЛОЖНОСТИ" (count_less_than_difficulty) ---
        btnCountLessThanDifficulty.addActionListener(e -> {
            ResourceManager i18n = ResourceManager.getInstance();

            // 1. Создаем выпадающий список из констант Enum Difficulty
            JComboBox<Difficulty> difficultyBox = new JComboBox<>(Difficulty.values());

            // 2. Показываем простое диалоговое окно с этим списком
            int result = JOptionPane.showConfirmDialog(
                    mainFrame,
                    difficultyBox,
                    i18n.getString("stat.count_diff"), // Заголовок окна
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            // 3. Если пользователь нажал "ОК"
            if (result == JOptionPane.OK_OPTION) {
                // Получаем выбранную сложность
                Difficulty selectedDifficulty = (Difficulty) difficultyBox.getSelectedItem();

                if (selectedDifficulty != null) {
                    // 4. Отправляем запрос на сервер в фоновом потоке
                    new SwingWorker<Response, Void>() {
                        @Override
                        protected Response doInBackground() throws Exception {

                            // ИСПРАВЛЕНИЕ: Передаем имя константы (например, "HARD") как аргумент
                            String difficultyArg = selectedDifficulty.name();
                            Request req = new Request("count_less_difficulty", difficultyArg, null, login, password);

                            udpClient.sendRequest(req);
                            return udpClient.receiveResponse();
                        }

                        @Override
                        protected void done() {
                            try {
                                Response resp = get();
                                if (resp.getSuccess()) {
                                    // Показываем результат (сервер должен прислать строку с количеством)
                                    JOptionPane.showMessageDialog(
                                            mainFrame,
                                            resp.getMessage(),
                                            i18n.getString("info.title"),
                                            JOptionPane.INFORMATION_MESSAGE
                                    );

                                    // Обновляем таблицу (на всякий случай)
                                    if (resp.getCollection() != null) {
                                        mainFrame.updateTableData(resp.getCollection());
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(
                                            mainFrame,
                                            resp.getMessage(),
                                            i18n.getString("error.title"),
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(mainFrame, "Ошибка связи: " + ex.getMessage());
                            }
                        }
                    }.execute();
                }
            }
        });

        btnPrintFieldDescendingMinimalPoint.addActionListener(e ->
                fetchAndShowText("print_field_descending_minimal_point", i18n.getString("stat.print_min"))
        );

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
