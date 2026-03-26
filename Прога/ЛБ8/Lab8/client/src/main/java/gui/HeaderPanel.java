package gui;

import i18n.LocaleChangeListener;
import i18n.ResourceManager;
import gui.LoginFrame; // Для кнопки выхода
import managers.UDPClient; // Для кнопки выхода
import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * Верхняя панель (шапка) приложения.
 * Отображает текущего пользователя слева, а настройки языка и выход — справа.
 */
public class HeaderPanel extends JPanel implements LocaleChangeListener {
    private final String login;
    private JLabel userLabel;
    private JLabel langLabel;
    private JComboBox<Locale> langComboBox;
    private JButton logoutButton;

    public HeaderPanel(String login, MainFrame mainFrame) {
        this.login = login;

        // 1. ИСПОЛЬЗУЕМ BorderLayout для разделения левой и правой части
        setLayout(new BorderLayout());

        // Добавим небольшие отступы по краям панели, чтобы текст не прилипал к окну
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        userLabel = new JLabel();


        add(userLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        langLabel = new JLabel();
        rightPanel.add(langLabel);

        // Настройка выпадающего списка языков
        langComboBox = new JComboBox<>(ResourceManager.SUPPORTED_LOCALES.toArray(new Locale[0]));
        langComboBox.setSelectedItem(ResourceManager.getInstance().getCurrentLocale());

        langComboBox.setRenderer(new DefaultListCellRenderer() {
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

        langComboBox.addActionListener(e -> {
            Locale selected = (Locale) langComboBox.getSelectedItem();
            if (selected != null) {
                ResourceManager.getInstance().setLocale(selected);
            }
        });
        rightPanel.add(langComboBox);

        // Настройка кнопки Выхода
        logoutButton = new JButton();
        logoutButton.addActionListener(e -> {
            mainFrame.dispose();
            // Открываем окно логина заново
            SwingUtilities.invokeLater(() -> new LoginFrame(mainFrame.getUdpClient()).setVisible(true));
        });
        rightPanel.add(logoutButton);

        // Добавляем правую панель на Восток (справа)
        add(rightPanel, BorderLayout.EAST);

        // Подписываемся на смену языка и переводим тексты
        ResourceManager.getInstance().addLocaleChangeListener(this);
        onLocaleChange();
    }

    @Override
    public void onLocaleChange() {
        ResourceManager i18n = ResourceManager.getInstance();

        // Обновляем тексты
        userLabel.setText(i18n.getString("main.label.user") + " " + login);
        langLabel.setText(i18n.getString("main.combo.lang"));
        logoutButton.setText(i18n.getString("main.btn.logout"));

        // Заставляем панель пересчитать свои размеры, если текст стал длиннее
        revalidate();
        repaint();
    }
}