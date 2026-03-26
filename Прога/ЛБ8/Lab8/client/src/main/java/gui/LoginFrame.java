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

    private JLabel loginLabel;
    private JLabel passLabel;
    private JLabel langLabel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginFrame(UDPClient udpClient) {
        this.udpClient = udpClient;

        setTitle(ResourceManager.getInstance().getString("auth.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initComponents();



        ResourceManager.getInstance().addLocaleChangeListener(this);
        onLocaleChange();

        pack();
        setLocationRelativeTo(null);
    }

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

    private void handleAuth() {
        ResourceManager i18n = ResourceManager.getInstance();

        String login = loginField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (login.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    i18n.getString("auth.error.empty"),
                    i18n.getString("error.title"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingWorker<Response, Void> worker = new SwingWorker<>() {
            @Override
            protected Response doInBackground() throws Exception {
                Request authRequest = new Request("auth", "", null, login, pass);
                udpClient.sendRequest(authRequest);
                return udpClient.receiveResponse();
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.getSuccess()) {
                        dispose();
                        MainFrame mainFrame = new MainFrame(udpClient, login, pass);
                        mainFrame.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                response.getMessage(),
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

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Увеличиваем отступы и шрифт для современного вида
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- ЛОГИН ---
        gbc.gridx = 0; gbc.gridy = 0;
        loginLabel = new JLabel();
        add(loginLabel, gbc);

        gbc.gridx = 1;
        loginField = new JTextField(20);
        add(loginField, gbc);

        // --- ПАРОЛЬ ---
        gbc.gridx = 0; gbc.gridy = 1;
        passLabel = new JLabel();
        add(passLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        // --- КНОПКИ ---
        Dimension btnSize = new Dimension(160, 50); // Делаем кнопки побольше

        gbc.gridx = 0; gbc.gridy = 2;
        loginButton = new JButton();
        loginButton.setPreferredSize(btnSize);
        add(loginButton, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        registerButton = new JButton();
        registerButton.setPreferredSize(btnSize);
        add(registerButton, gbc);

        // --- ЯЗЫК ---
        gbc.gridx = 0; gbc.gridy = 3;
        langLabel = new JLabel();
        add(langLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JComboBox<Locale> langCombo = new JComboBox<>(ResourceManager.SUPPORTED_LOCALES.toArray(new Locale[0]));
        langCombo.setPreferredSize(new Dimension(200, 40));

        langCombo.setSelectedItem(ResourceManager.getInstance().getCurrentLocale());

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

        langCombo.addActionListener(e -> {
            Locale selected = (Locale) langCombo.getSelectedItem();
            if (selected != null) {
                ResourceManager.getInstance().setLocale(selected);
            }
        });

        add(langCombo, gbc);

        loginButton.addActionListener(e -> handleAuth());
        registerButton.addActionListener(e -> handleAuth());
    }
}