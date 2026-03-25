package gui;

import i18n.ResourceManager;
import models.LabWork;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class LabWorkTableModel extends AbstractTableModel {
    private List<LabWork> labWorks = new ArrayList<>();

    private final String[] columnKeys = {
            "table.col.id", "table.col.name", "table.col.owner",
            "table.col.x", "table.col.y", "table.col.point",
            "table.col.diff", "table.col.date", "table.col.desc",
            "table.col.tuned", "table.col.author"
    };

    public LabWorkTableModel(List<LabWork> initialData) {
        if (initialData != null) {
            this.labWorks = new ArrayList<>(initialData);
        }
    }

    public void setLabWorks(List<LabWork> newLabWorks) {
        this.labWorks.clear();
        if (newLabWorks != null) {
            this.labWorks.addAll(newLabWorks);
        }

        fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return columnKeys.length;
    }

    @Override
    public int getRowCount() {
        return labWorks.size();
    }

    @Override
    public String getColumnName(int column) {
        //сразу переводим через ресурс менеджер
        return ResourceManager.getInstance().getString(columnKeys[column]);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LabWork lab = labWorks.get(rowIndex);
        ResourceManager i18n = ResourceManager.getInstance();

        switch (columnIndex) {
            case 0: return lab.getId();
            case 1: return lab.getName();
            case 2: return lab.getOwnerLogin() != null ? lab.getOwnerLogin() : "system";
            case 3: return lab.getCoordinates().getX();
            case 4: return lab.getCoordinates().getY();

            // Форматируем дробное число по локали (например, 10,5 или 10.5)
            case 5: return i18n.formatNumber(lab.getMinimalPoint());

            // Enum выводим как строку
            case 6: return lab.getDifficulty().toString();

            // Форматируем дату по локали
            case 7: return i18n.formatDate(lab.getCreationDate());

            // Описание (может быть null)
            case 8: return lab.getDescription() != null ? lab.getDescription() : "";

            // TunedInWorks (может быть null)
            case 9: return lab.getTunedInWorks() != null ? lab.getTunedInWorks() : "";

            // Автор (выводим только имя для компактности, или пустоту)
            case 10: return lab.getAuthor() != null ? lab.getAuthor().getName() : "";

            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (labWorks.isEmpty()) {
            return Object.class;
        }
        Object value = getValueAt(0, columnIndex);
        return value != null ? value.getClass() : Object.class;
    }

}
