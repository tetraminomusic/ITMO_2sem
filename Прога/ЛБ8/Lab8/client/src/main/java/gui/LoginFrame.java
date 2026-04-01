package gui;

import javax.swing.*;
import managers.UDPClient;
import i18n.LocaleChangeListener;
import i18n.ResourceManager;
import network.Request;
import network.Response;

import java.awt.*;
import java.util.Locale;

/**
 * Окно авторизации/регистрации.
 * Обеспечивает ввод данных пользователя и смену локали интерфейса.
 */
public class LoginFrame extends JFrame implements LocaleChangeListener {
    private final UDPClient udpClient;

    //все графические элементы приложения
    private JLabel loginLabel;
    private JLabel passLabel;
    private JLabel langLabel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    /**
     * Конструктор класса
     * Отвечает за настройку JFrame, инициализации компонентов через метод initComponents(), подписывается на локализации РесурсМанагера.
     */
    public LoginFrame(UDPClient udpClient) {
        this.udpClient = udpClient;

        //настройка окна приложения
        setTitle(ResourceManager.getInstance().getString("auth.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        //инициализация объектов
        initComponents();

        //подписка на локализацию и первичный вызов установки локализации
        ResourceManager.getInstance().addLocaleChangeListener(this);
        onLocaleChange();

        //центрирование и сжатие окна по содержимому
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Метод, который вызывается при глобальной смене языка
     * По сути просто меняет везде текст и упаковывает фрейм по новой
     */
    @Override
    public void onLocaleChange() {
        ResourceManager i18n = ResourceManager.getInstance();

        setTitle(i18n.getString("auth.title"));
        loginLabel.setText(i18n.getString("auth.label.login"));
        passLabel.setText(i18n.getString("auth.label.password"));
        loginButton.setText(i18n.getString("auth.btn.login"));
        registerButton.setText(i18n.getString("auth.btn.register"));
        langLabel.setText(i18n.getString("main.combo.lang"));

        pack(); // Окно пересчитывает размер при смене языка
    }


    /**
     * Отвечает за отправку запроса серверу об аутентификации
     */
    private void handleAuth() {
        ResourceManager i18n = ResourceManager.getInstance();

        //получаем данные, которые ввёл пользователь
        String login = loginField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        //валидация
        if (login.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    i18n.getString("auth.error.empty"),
                    i18n.getString("error.title"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Асинхронно отправляем запрос серверу на логин
        SwingWorker<Response, Void> worker = new SwingWorker<>() {
            @Override
            protected Response doInBackground() throws Exception {
                //этот метод выполняется фоново без блокирования гуи
                Request authRequest = new Request("auth", "", null, login, pass);
                udpClient.sendRequest(authRequest);
                return udpClient.receiveResponse();
            }

            @Override
            protected void done() {
                //этот метод выполняется в главном потоке гуи
                try {
                    //получаем результат от doInBackGround
                    Response response = get();
                    if (response.getSuccess()) {
                        dispose(); //уничтожает текущее окно
                        MainFrame mainFrame = new MainFrame(udpClient, login, pass);
                        mainFrame.setVisible(true); //открывает основное окно
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                i18n.getString(response.getMessage()),
                                i18n.getString("error.title"),
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Network Error: " + e.getMessage(),
                            i18n.getString("error.title"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    /**
     * Метод, инициализирующий текущий фрейм
     */
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Увеличиваем отступы и шрифт для современного вида
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //логин (текст + поле ввода)
        gbc.gridx = 0; gbc.gridy = 0;
        loginLabel = new JLabel();
        add(loginLabel, gbc);

        gbc.gridx = 1;
        loginField = new JTextField(20);
        add(loginField, gbc);

        //пароль the same
        gbc.gridx = 0; gbc.gridy = 1;
        passLabel = new JLabel();
        add(passLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        //кнопки
        Dimension btnSize = new Dimension(160, 50);

        gbc.gridx = 0; gbc.gridy = 2;
        loginButton = new JButton();
        loginButton.setPreferredSize(btnSize);
        add(loginButton, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        registerButton = new JButton();
        registerButton.setPreferredSize(btnSize);
        add(registerButton, gbc);

        //язык
        gbc.gridx = 0; gbc.gridy = 3;
        langLabel = new JLabel();
        add(langLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JComboBox<Locale> langCombo = new JComboBox<>(ResourceManager.SUPPORTED_LOCALES.toArray(new Locale[0]));
        langCombo.setPreferredSize(new Dimension(200, 40));

        //устанавливаем текущий элемент списка как текущую локаль
        langCombo.setSelectedItem(ResourceManager.getInstance().getCurrentLocale());

        //Перерисовывает названия языков в большой буквы
        langCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Locale l = (Locale) value;
                String lang = l.getDisplayLanguage(l);
                if (lang.length() > 0) {
                    lang = lang.substring(0, 1).toUpperCase() + lang.substring(1);
                }
                return super.getListCellRendererComponent(list, lang, index, isSelected, cellHasFocus);
            }
        });

        //слушатель
        langCombo.addActionListener(e -> {
            Locale selected = (Locale) langCombo.getSelectedItem();
            if (selected != null) {
                ResourceManager.getInstance().setLocale(selected);
            }
        });

        add(langCombo, gbc);

        //подписываем слушателей на кнопки. Слушатели будут выполнять метод handleAuth()
        loginButton.addActionListener(e -> handleAuth());
        registerButton.addActionListener(e -> handleAuth());
    }
}