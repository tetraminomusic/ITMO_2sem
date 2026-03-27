package gui;

import i18n.LocaleChangeListener;
import i18n.ResourceManager;
import managers.UDPClient;
import models.LabWork;
import network.Request;
import network.Response;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame implements LocaleChangeListener {
    private final UDPClient udpClient;
    private final String login;
    private final String password;

    private HeaderPanel headerPanel;
    private CommandPanel commandPanel;
    private MainPanel mainPanel;

    //локально храним коллекцию
    private List<LabWork> collection = new ArrayList<>();

    public MainFrame(UDPClient udpClient, String login, String password) {
        this.udpClient = udpClient;
        this.login = login;
        this.password = password;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800,600));

        ResourceManager.getInstance().addLocaleChangeListener(this);

        //Инициализируем пустые компоненты
        initComponents();

        //устанавливаем тексты
        ResourceManager.getInstance().addLocaleChangeListener(this);
        onLocaleChange();

        //запускаем загрузку данных с сервера
        loadInitialCollection();

        //центр
        setLocationRelativeTo(null);

        Timer timer = new Timer(5000, e -> {
            loadInitialCollection();
        });
        timer.start();

    }

    /**
     * Метод, который в фоновом потоке запрашивает коллекцию от сервера
     */
    public void loadInitialCollection() {
        new SwingWorker<List<LabWork>, Void>() {
            @Override
            protected List<LabWork> doInBackground() throws Exception {
                //нужна команда show
                Request req = new Request("show", "", null, login, password);
                udpClient.sendRequest(req);
                Response response = udpClient.receiveResponse();

                if (response.getSuccess()) {
                    return response.getCollection();
                } else {
                    throw new RuntimeException("Сервер вернул ошибку: " + response.getMessage());
                }
            }

            @Override
            protected void done() {
                try {
                    collection = get();
                    mainPanel.updateTableData(collection);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Ошибка загрузки: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        headerPanel = new HeaderPanel(login, this);
        add(headerPanel, BorderLayout.NORTH);

        commandPanel = new CommandPanel(udpClient, login, password, this);
        add(commandPanel, BorderLayout.WEST);

        mainPanel = new MainPanel(this);
        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public void onLocaleChange() {
        setTitle(ResourceManager.getInstance().getString("app.title"));
    }

    public MainPanel getMainPanel() {
        return mainPanel;
    }

    public UDPClient getUdpClient() {
        return udpClient;
    }

    public void updateTableData(List<LabWork> newData) {

        this.collection = newData;

        if (mainPanel != null) {
            mainPanel.updateTableData(newData);
        }
    }

    public LabWork getLabWorkById(int id) {
        if (collection == null) return null;

        return collection.stream()
                .filter(lab -> lab.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public String getSelectedId() {
        //Проверяем, создана ли вообще панель с таблицей
        if (mainPanel == null || mainPanel.getTable() == null) {
            return null;
        }

        JTable table = mainPanel.getTable();

        //Получаем индекс выделенной строки на экране
        int selectedRow = table.getSelectedRow();

        // Если ничего не выделено, метод возвращает -1
        if (selectedRow == -1) {
            return null;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);

        Object idValue = table.getModel().getValueAt(modelRow, 0);

        return idValue != null ? idValue.toString() : null;
    }
}
