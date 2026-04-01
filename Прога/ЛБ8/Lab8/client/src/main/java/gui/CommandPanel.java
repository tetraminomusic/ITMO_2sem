package gui;

import action.*;
import action.askersAction.*;
import action.executeScriptAction.ExecuteScriptAction;
import action.informAction.*;
import action.executeScriptAction.ExecuteScriptCommand;
import i18n.LocaleChangeListener;
import i18n.ResourceManager;
import managers.UDPClient;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Левая панель главного окна.
 * Отвечает за создание кнопок команд, их локализацию и привязку действий (Actions).
 */
public class CommandPanel extends JPanel implements LocaleChangeListener {
    private final UDPClient udpClient;
    private final String login;
    private final String password;
    private final MainFrame mainFrame;
    private final Frame parentFrame;

    //Кнопки управления
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

    // Объект для выполнения скриптов (передается в соответствующий Action)
    private final ExecuteScriptCommand scriptExecutor;

    /**
     * Конструктор панели команд.
     *
     * @param udpClient сетевой клиент для передачи в Actions.
     * @param login логин пользователя.
     * @param password пароль пользователя.
     * @param mainFrame ссылка на главное окно для вывода диалогов и обновления данных.
     */
    public CommandPanel(UDPClient udpClient, String login, String password, MainFrame mainFrame) {
        this.udpClient = udpClient;
        this.login = login;
        this.password = password;
        this.mainFrame = mainFrame;
        this.parentFrame = mainFrame;

        // Инициализируем исполнителя скриптов
        List<String> objectCmds = Arrays.asList("insert", "update", "replace_if_greater", "remove_lower");
        this.scriptExecutor = new ExecuteScriptCommand(udpClient, new managers.LabWorkAsker(null), objectCmds);

        // Настройка визуального расположения (вертикальный список с отступами)
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //Создаем кнопки и расставляем их на панели
        initComponents();

        //Привязываем логику (классы Action) к кнопкам
        setupListeners();

        //Подписываемся на смену языка и переводим тексты в первый раз
        ResourceManager.getInstance().addLocaleChangeListener(this);
        onLocaleChange();
    }

    /**
     * Инициализация, стилизация и добавление кнопок на панель.
     */
    private void initComponents() {
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

        // Применяем единый стиль ко всем кнопкам
        JButton[] allButtons = {
                btnInsert, btnUpdate, btnRemove, btnClear, btnRemoveLower,
                btnReplaceIfGreater, btnHistory, btnCountLessThanDifficulty,
                btnGroupByMinimalPoint, btnPrintFieldDescendingMinimalPoint, btnExecuteScript
        };
        for (JButton btn : allButtons) {
            styleButton(btn);
        }

        // --- Расстановка кнопок с отступами ---

        // Базовые операции (CRUD)
        add(btnInsert);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnUpdate);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnRemove);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnClear);
        add(Box.createRigidArea(new Dimension(0, 20))); // Больший отступ между блоками

        // Сложные операции изменения
        add(btnRemoveLower);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnReplaceIfGreater);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // Операции статистики и информации
        add(btnHistory);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnCountLessThanDifficulty);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnGroupByMinimalPoint);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnPrintFieldDescendingMinimalPoint);
        add(Box.createRigidArea(new Dimension(0, 30))); // Отделяем системную команду

        // Системные команды
        add(btnExecuteScript);
    }

    /**
     * Назначение обработчиков событий (Action) для каждой кнопки.
     */
    private void setupListeners() {
        btnInsert.addActionListener(new InsertAction(mainFrame, udpClient, login, password));
        btnUpdate.addActionListener(new UpdateAction(mainFrame, udpClient, login, password));
        btnRemove.addActionListener(new RemoveAction(mainFrame, udpClient, login, password));
        btnClear.addActionListener(new ClearAction(mainFrame, udpClient, login, password));

        btnRemoveLower.addActionListener(new RemoveLowerAction(mainFrame, udpClient, login, password));
        btnReplaceIfGreater.addActionListener(new ReplaceIfGreaterAction(mainFrame, udpClient, login, password));

        btnCountLessThanDifficulty.addActionListener(new CountLessThanDifficultyAction(mainFrame, udpClient, login, password));
        btnHistory.addActionListener(new HistoryAction(mainFrame, udpClient, login, password));
        btnGroupByMinimalPoint.addActionListener(new GroupByMinimalPointAction(mainFrame, udpClient, login, password));
        btnPrintFieldDescendingMinimalPoint.addActionListener(new PrintFieldDescendingAction(mainFrame, udpClient, login, password));

        // Скрипт (передаем дополнительный параметр scriptExecutor)
        btnExecuteScript.addActionListener(new ExecuteScriptAction(mainFrame, udpClient, login, password, scriptExecutor));
    }

    /**
     * Перевод текстов на кнопках при смене языка.
     */
    @Override
    public void onLocaleChange() {
        ResourceManager i18n = ResourceManager.getInstance();

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

    /**
     * Вспомогательный метод для стандартизации внешнего вида кнопок.
     */
    private void styleButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Short.MAX_VALUE, 35));
        // Шрифт задается глобально в ClientMain (UIManager.put), поэтому здесь setFont не обязателен.
    }
}