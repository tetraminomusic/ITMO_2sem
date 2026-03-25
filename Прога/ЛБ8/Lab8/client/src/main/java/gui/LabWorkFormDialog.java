package gui;

import i18n.ResourceManager;
import models.Coordinates;
import models.Difficulty;
import models.LabWork;
import models.Person;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LabWorkFormDialog extends JDialog {

    private final JTextField nameField = new JTextField(20);
    private final JTextField xField = new JTextField(10);
    private final JTextField yField = new JTextField(10);
    private final JTextField minPointField = new JTextField(10);
    private final JTextField descField = new JTextField(20);
    private final JTextField tunedWorksField = new JTextField(10);
    private final JComboBox<Difficulty> difficultyBox = new JComboBox<>(Difficulty.values());

    //Person
    private final JTextField authorNameField = new JTextField(20);
    private final JTextField authorBirthdayField = new JTextField(15);
    private final JTextField authorHeightField = new JTextField(10);
    private final JTextField authorWeightField = new JTextField(10);
    private final JTextField authorPassportField = new JTextField(20);

    private JPanel authorDetailsPanel;
    private LabWork result = null;
    private final ResourceManager i18n = ResourceManager.getInstance();

    public LabWorkFormDialog(Frame parent, String title, LabWork initialData) {
        super(parent, title, true);

        // 1. Главный контейнер с отступами
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 2. Панель формы
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Отступы между ячейками
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addFormRow(formPanel, gbc, row++, i18n.getString("form.label.name"), nameField);
        addFormRow(formPanel, gbc, row++, i18n.getString("form.label.coordX"), xField);
        addFormRow(formPanel, gbc, row++, i18n.getString("form.label.coordY"), yField);
        addFormRow(formPanel, gbc, row++, i18n.getString("form.label.minPoint"), minPointField);
        addFormRow(formPanel, gbc, row++, i18n.getString("form.label.desc"), descField);
        addFormRow(formPanel, gbc, row++, i18n.getString("form.label.tuned"), tunedWorksField);

        // Сложность (отдельная обработка, так как это ComboBox)
        gbc.gridy = row++;
        gbc.gridx = 0; gbc.weightx = 0;
        formPanel.add(new JLabel(i18n.getString("form.label.difficulty")), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(difficultyBox, gbc);

        // Разделитель
        gbc.gridy = row++; gbc.gridx = 0; gbc.gridwidth = 2;
        formPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1; // Возвращаем ширину в 1 колонку

        // Автор (триггер)
        addFormRow(formPanel, gbc, row++, i18n.getString("form.label.author.name"), authorNameField);

        // 3. СКРЫТАЯ ПАНЕЛЬ АВТОРА
        authorDetailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints authGbc = new GridBagConstraints();
        authGbc.fill = GridBagConstraints.HORIZONTAL;
        authGbc.insets = new Insets(5, 0, 5, 10);

        int authRow = 0;
        addFormRow(authorDetailsPanel, authGbc, authRow++, i18n.getString("form.label.author.birthday"), authorBirthdayField);
        addFormRow(authorDetailsPanel, authGbc, authRow++, i18n.getString("form.label.author.height"), authorHeightField);
        addFormRow(authorDetailsPanel, authGbc, authRow++, i18n.getString("form.label.author.weight"), authorWeightField);
        addFormRow(authorDetailsPanel, authGbc, authRow, i18n.getString("form.label.author.passport"), authorPassportField);

        authorDetailsPanel.setVisible(false);
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2;
        formPanel.add(authorDetailsPanel, gbc);

        // Логика появления
        authorNameField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                SwingUtilities.invokeLater(() -> {
                    boolean show = !authorNameField.getText().trim().isEmpty();
                    if (authorDetailsPanel.isVisible() != show) {
                        authorDetailsPanel.setVisible(show);
                        pack();
                    }
                });
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });

        // 4. Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton(i18n.getString("form.btn.save"));
        JButton cancelBtn = new JButton(i18n.getString("btn.cancel"));
        saveBtn.addActionListener(e -> onSave());
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        if (initialData != null) fillData(initialData);

        contentPane.add(formPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        add(contentPane);

        pack();
        setLocationRelativeTo(parent);


    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(field, gbc);
    }

    private void fillData(LabWork lab) {
        nameField.setText(lab.getName());
        xField.setText(String.valueOf(lab.getCoordinates().getX()));
        yField.setText(String.valueOf(lab.getCoordinates().getY()));
        minPointField.setText(String.valueOf(lab.getMinimalPoint()));
        descField.setText(lab.getDescription() == null ? "" : lab.getDescription());
        tunedWorksField.setText(lab.getTunedInWorks() == null ? "" : String.valueOf(lab.getTunedInWorks()));
        difficultyBox.setSelectedItem(lab.getDifficulty());

        if (lab.getAuthor() != null) {
            Person a = lab.getAuthor();
            authorNameField.setText(a.getName());
            authorBirthdayField.setText(a.getBirthday().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            authorHeightField.setText(String.valueOf(a.getHeight()));
            authorWeightField.setText(a.getWeight() == null ? "" : String.valueOf(a.getWeight()));
            authorPassportField.setText(a.getPassportID() == null ? "" : a.getPassportID());
        }
    }

    private void onSave() {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) throw new Exception(i18n.getString("err.validation.empty"));

            long x = Long.parseLong(xField.getText().trim());
            if (x > 162) throw new Exception(i18n.getString("err.validation.invalidX"));

            long y = Long.parseLong(yField.getText().trim());

            float minPoint = Float.parseFloat(minPointField.getText().trim());
            if (minPoint <= 0) throw new Exception(i18n.getString("err,validation.invalidPoint"));

            Person author = null;
            if (!authorNameField.getText().trim().isEmpty()) {
                LocalDateTime bday = LocalDate.parse(authorBirthdayField.getText().trim(),
                        DateTimeFormatter.ofPattern("dd.MM.yyyy")).atStartOfDay();
                author = new Person(authorNameField.getText().trim(), bday,
                        Float.parseFloat(authorHeightField.getText().trim()),
                        authorWeightField.getText().trim().isEmpty() ? null : Double.parseDouble(authorWeightField.getText().trim()),
                        authorPassportField.getText().trim());
            }

            result = new LabWork(0, name, new Coordinates(x, y), LocalDateTime.now(),
                    minPoint, descField.getText(),
                    tunedWorksField.getText().isEmpty() ? null : Integer.parseInt(tunedWorksField.getText()),
                    (Difficulty) difficultyBox.getSelectedItem(), author);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, i18n.getString("error.title") + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public LabWork getResult() {
        return result;
    }
}
