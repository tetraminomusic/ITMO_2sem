package gui;

import i18n.LocaleChangeListener;
import i18n.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class HeaderPanel extends JPanel implements LocaleChangeListener {
    private final String login;
    private JLabel userLabel;
    private JComboBox<Locale> langComboBox;
    private JButton logoutButton;

    public HeaderPanel(String login, MainFrame mainFrame) {
        this.login = login;
        setLayout(new FlowLayout(FlowLayout.RIGHT));

        //Надпись для пользователя
        userLabel = new JLabel("Пользователь: " + login);
        add(userLabel);


        //выпадающий список
        langComboBox = new JComboBox<>(ResourceManager.SUPPORTED_LOCALES.toArray(new Locale[0]));

        //устанавливаем текущую локаль
        langComboBox.setSelectedItem(ResourceManager.getInstance().getCurrentLocale());

        //для отображения текущего языка в списке
        langComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Locale l = (Locale) value;
                //Получаем стандартное название ("русский")
                String language = l.getDisplayLanguage(l);

                //Делаем первую букву заглавной ("Русский")
                if (language.length() > 0) {
                    language = language.substring(0, 1).toUpperCase() + language.substring(1);
                }

                return super.getListCellRendererComponent(list, language, index, isSelected, cellHasFocus);
            }
        });

        langComboBox.addActionListener(e -> {
            Locale selected = (Locale) langComboBox.getSelectedItem();
            if (selected != null) {
                ResourceManager.getInstance().setLocale(selected);
            }
        });

        add(langComboBox);

        //кнопка выхода
        logoutButton = new JButton("Выйти");

        logoutButton.addActionListener(e -> {
            mainFrame.dispose();

            SwingUtilities.invokeLater(() -> {
                new LoginFrame(mainFrame.getUdpClient()).setVisible(true);
            });

            System.out.println("Пользователь " + login + " вышел из системы");
        });

        add(logoutButton);

        ResourceManager.getInstance().addLocaleChangeListener(this);

        onLocaleChange();
    }

    @Override
    public void onLocaleChange() {
        ResourceManager i18n = ResourceManager.getInstance();

        userLabel.setText(i18n.getString("main.label.user") + " " + login);

        logoutButton.setText(i18n.getString("main.btn.logout"));
    }
}
