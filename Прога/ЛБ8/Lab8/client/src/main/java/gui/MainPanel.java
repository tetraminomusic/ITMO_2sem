package gui;

import i18n.LocaleChangeListener;
import i18n.ResourceManager;
import models.LabWork;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainPanel extends JPanel implements LocaleChangeListener {
    //таблица
    private JTabbedPane tabbedPane;
    private JPanel tableTab;
    private JLabel filterLabel;
    private JTextField filterField;
    private JTable labTable;
    private LabWorkTableModel tableModel;
    private TableRowSorter<LabWorkTableModel> sorter;

    //карта
    private MapPanel mapPanel;
    private MainFrame mainFrame;

    /**
     * Инициализирует компоненты контейнера и подписывает изменение локали
     */
    public MainPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        initComponents();

        ResourceManager.getInstance().addLocaleChangeListener(this);
        onLocaleChange();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        //Вкладка Таблицы
        tableTab = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterLabel = new JLabel();
        filterPanel.add(filterLabel);
        filterField = new JTextField(20);
        filterPanel.add(filterField);
        tableTab.add(filterPanel, BorderLayout.NORTH);

        tableModel = new LabWorkTableModel(new ArrayList<>());
        labTable = new JTable(tableModel);

        // Настройка сортера
        sorter = new TableRowSorter<>(tableModel);
        labTable.setRowSorter(sorter);

        // Внешний вид
        labTable.setFillsViewportHeight(true);
        labTable.setRowHeight(30); // Сделаем строки повыше для Comic Sans
        labTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane tableScrollPane = new JScrollPane(labTable);
        tableTab.add(tableScrollPane, BorderLayout.CENTER);

        // Слушатель фильтра
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });

        //Вкладка Карты
        mapPanel = new MapPanel(mainFrame);


        //добавляем вкладки
        tabbedPane.add("", tableTab);
        tabbedPane.add("", mapPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Применяет оформление к колонкам.
     * Нужно вызывать после каждой смены структуры таблицы.
     */
    private void setupTableAppearance() {
        //Создаем рендерер для центрирования
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        //Применяем центрирование ко ВСЕМ столбцам (от 0 до 10)
        for (int i = 0; i < labTable.getColumnCount(); i++) {
            labTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        //Явно задаем ширину для узких колонок
        //Устанавливаем и Preferred (желаемую), и Minimum ширину
        setColumnWidth(0, 40);  // ID
        setColumnWidth(3, 50);  // X
        setColumnWidth(4, 50);  // Y
        setColumnWidth(5, 80);  // Мин. балл
        setColumnWidth(6, 120); // Сложность

        // Для текстовых колонок (Название, Описание) пусть работает авто-подбор
        adjustColumnWidths(labTable);
    }

    /**
     * Вспомогательный метод для задания ширины конкретной колонке
     */
    private void setColumnWidth(int index, int width) {
        if (labTable.getColumnCount() > index) {
            TableColumn col = labTable.getColumnModel().getColumn(index);
            col.setPreferredWidth(width);
            col.setMinWidth(width); // Гарантирует, что колонка не схлопнется совсем
        }
    }

    public void updateTableData(List<LabWork> newData) {
        //таблица получила данные
        tableModel.setLabWorks(newData);

        //эти же данные получает и карта
        if (mapPanel != null) {
            mapPanel.updateData(newData);
        }
        // После обновления данных подгоняем ширину
        SwingUtilities.invokeLater(() -> adjustColumnWidths(labTable));
    }

    @Override
    public void onLocaleChange() {
        ResourceManager i18n = ResourceManager.getInstance();

        tabbedPane.setTitleAt(0, i18n.getString("main.tab.table"));
        tabbedPane.setTitleAt(1, i18n.getString("main.tab.map"));
        filterLabel.setText(i18n.getString("main.label.filter") + ": ");

        if (tableModel != null) {
            tableModel.fireTableStructureChanged();
            setupTableAppearance();
        }
    }

    private void applyFilter() {
        String text = filterField.getText();
        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // (?i) для регистронезависимости
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    /**
     * Метод, который пробегает по всем строкам, измеряет ширинку текста в пикселях и раздвигает колонки под нужный размер
     */
    private void adjustColumnWidths(JTable table) {
        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);

            // Считаем ширину заголовка
            Object headerValue = tableColumn.getHeaderValue();
            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(table, headerValue, false, false, -1, column);
            int width = headerComp.getPreferredSize().width + 10;

            // Считаем ширину самой длинной ячейки в этой колонке
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 10, width);
            }

            // Ограничиваем слишком широкие столбцы (например, описание)
            tableColumn.setPreferredWidth(Math.min(width, 300));
        }
    }

    public JTable getTable() {
        return labTable;
    }
}