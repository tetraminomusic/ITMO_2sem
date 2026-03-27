package gui;

import i18n.ResourceManager;
import models.LabWork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class MapPanel extends JPanel {

    private LabWork hoveredLab = null;
    private float hoverScale = 1.0f;
    private final Timer hoverTimer;

    private List<LabWork> labWorks = new ArrayList<>();
    private final Map<String, Color> userColors = new ConcurrentHashMap<>();
    private final MainFrame mainFrame;

    private double offsetX = 0;
    private double offsetY = 0;

    public MapPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(Color.WHITE);

        //клик мыши
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleSelection(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Вычисляем отклонение мыши от центра панели
                // Делим на 20, чтобы движение было очень тонким и приятным (не дерганым)
                offsetX = (e.getX() - getWidth() / 2.0) / 25.0;
                offsetY = (e.getY() - getHeight() / 2.0) / 25.0;

                // Перерисовываем панель, чтобы применить смещение
                repaint();

                // Также проверяем hover (как мы делали раньше)
                checkHover(e.getX(), e.getY());
            }
        });

        hoverTimer = new Timer(30, e -> {
            if (hoveredLab != null) {
                hoverScale = (float) (1.1 + 0.1 * Math.sin(System.currentTimeMillis() / 150.0));
                repaint();
            }
        });
        hoverTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        //включаем сглаживание
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        for (LabWork lab: labWorks) {
            //устанавливаем определённые цвета на каждый объект конкретного юзера
            Color color = getUserColor(lab.getOwnerLogin());
            g2.setColor(color);

            //коорды объекта
            int x = centerX + (int) lab.getCoordinates().getX() + (int)offsetX;
            int y = centerY - (int) lab.getCoordinates().getY() + (int)offsetY;

            //размер
            int baseSize = (int) Math.max(30, Math.min(100, lab.getMinimalPoint()));
            int animationSize = baseSize;

            int currentSize;

            if (lab == hoveredLab) {
                currentSize = (int) (baseSize * hoverScale);
                // Рисуем легкую тень или свечение под активным объектом
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillOval(x - currentSize/2 + 5, y - currentSize/2 + 5, currentSize, currentSize);
                g2.setColor(color);
                g2.drawString("ID: " + lab.getId(), x - 23, y - (currentSize / 2) - 5);
            } else {
                currentSize = baseSize;
                g2.drawString("ID: " + lab.getId(), x - 23, y - (animationSize / 2) - 5);
            }

            //рисуем заливку
            g2.fillOval(x - currentSize / 2, y - currentSize / 2, currentSize, currentSize);

            //рисуем обводку
            g2.setColor(Color.BLACK);
            g2.drawOval(x - currentSize / 2, y - currentSize / 2, currentSize, currentSize);

            //пишем айдишник



        }
    }

    private Color getUserColor(String login) {
        if (login == null) return Color.GRAY;
        return userColors.computeIfAbsent(login, k -> {
            Random r = new Random(k.hashCode());
            return new Color(r.nextInt(200), r.nextInt(200), r.nextInt(200));
        });
    }

    /**
     * Обрабатывает клик пользователя по карте.
     * Если клик попал в область объекта, выводит подробную информацию о нем.
     *
     * @param mouseX координата X клика мыши на панели.
     * @param mouseY координата Y клика мыши на панели.
     */
    private void handleSelection(int mouseX, int mouseY) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        LabWork found = null;

        for (int i = labWorks.size() - 1; i >= 0; i--) {
            LabWork lab = labWorks.get(i);
            int x = centerX + (int) lab.getCoordinates().getX();
            int y = centerY - (int) lab.getCoordinates().getY();
            int size = (int) Math.max(30, Math.min(100, lab.getMinimalPoint()));
            if (Point.distance(mouseX, mouseY, x, y) < size / 2.0) {
                found = lab;
                break;
            }
        }

        if (found != null) {
            ResourceManager i18n = ResourceManager.getInstance();
            Color userColor = getUserColor(found.getOwnerLogin());
            String hexColor = String.format("#%02x%02x%02x", userColor.getRed(), userColor.getGreen(), userColor.getBlue());

            StringBuilder sb = new StringBuilder();
            // Увеличили ширину для лучшего восприятия
            sb.append("<html><div style='width: 380px; padding: 10px; font-family: sans-serif; background-color: #ffffff;'>");

            // Название и ID
            sb.append("<h1 style='margin:0; color:").append(hexColor).append("; font-size: 24px;'>")
                    .append(found.getName()).append("</h1>");
            sb.append("<div style='color: #95a5a6; font-size: 11px; margin-bottom: 5px;'>ID: ").append(found.getId()).append("</div>");

            // Жирная разделительная линия
            sb.append("<div style='height: 4px; background: ").append(hexColor).append("; margin-bottom: 15px;'></div>");

            // Таблица данных
            sb.append("<table border='0' cellspacing='0' cellpadding='4' style='width:100%;'>");
            appendNiceRow(sb, i18n.getString("table.col.owner"), found.getOwnerLogin());
            appendNiceRow(sb, i18n.getString("table.col.point"), i18n.formatNumber(found.getMinimalPoint()));
            appendNiceRow(sb, i18n.getString("table.col.diff"), found.getDifficulty().toString());
            appendNiceRow(sb, i18n.getString("table.col.date"), i18n.formatDate(found.getCreationDate()));
            appendNiceRow(sb, "Coordinates", "X: " + found.getCoordinates().getX() + ", Y: " + found.getCoordinates().getY());

            if (found.getTunedInWorks() != null) {
                appendNiceRow(sb, i18n.getString("table.col.tuned"), found.getTunedInWorks().toString());
            }
            sb.append("</table>");

            // Блок Описания
            if (found.getDescription() != null && !found.getDescription().isEmpty()) {
                sb.append("<div style='margin-top: 15px; padding: 10px; background: #fdfdfd; border: 1px solid #eee; border-left: 5px solid ").append(hexColor).append(";'>")
                        .append("<span style='color: #7f8c8d; font-size: 11px;'>").append(i18n.getString("table.col.desc")).append(":</span><br>")
                        .append("<span style='font-size: 14px;'>").append(found.getDescription()).append("</span></div>");
            }

            // Блок АВТОРА
            if (found.getAuthor() != null) {
                models.Person a = found.getAuthor();
                sb.append("<div style='margin-top: 20px; padding: 12px; border: 1px dashed #cccccc; border-radius: 8px;'>");
                sb.append("<b style='color:").append(hexColor).append("; font-size: 16px;'>")
                        .append(i18n.getString("table.col.author")).append("</b><br>");

                sb.append("<div style='margin-top: 5px; color: #34495e;'>");
                sb.append("<b>•</b> ").append(a.getName()).append("<br>");

                // Форматируем ДЕНЬ РОЖДЕНИЯ без времени (используем substring или свой форматтер)
                String bday = i18n.formatDate(a.getBirthday()).split(" ")[0]; // Берем только часть до пробела
                sb.append("<b>•</b> ").append(bday).append("<br>");

                sb.append("<b>•</b> ").append(i18n.getString("form.label.author.height")).append(" ").append(a.getHeight()).append("<br>");

                if (a.getWeight() != null) {
                    sb.append("<b>•</b> ").append(i18n.getString("form.label.author.weight")).append(" ").append(a.getWeight()).append("<br>");
                }
                if (a.getPassportID() != null && !a.getPassportID().isEmpty()) {
                    sb.append("<b>•</b> Паспорт: <span style='font-family: monospace;'>").append(a.getPassportID()).append("</span>");
                }
                sb.append("</div></div>");
            }

            sb.append("</div></html>");

            JOptionPane.showMessageDialog(this, sb.toString(),
                    i18n.getString("info.title"), JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void appendNiceRow(StringBuilder sb, String label, String value) {
        sb.append("<tr>")
                .append("<td style='color: #7f8c8d; font-size: 13px; width: 40%;'>").append(label).append("</td>")
                .append("<td style='color: #2c3e50; font-size: 13px;'><b>").append(value).append("</b></td>")
                .append("</tr>");
    }

    public void updateData(List<LabWork> newData) {
        this.labWorks = new ArrayList<>(newData);
        repaint();
    }

    private void appendRow(StringBuilder sb, String label, String value) {
        sb.append("<tr><td style='color:gray;'>").append(label).append(":</td>")
                .append("<td><b>").append(value).append("</b></td></tr>");
    }

    private void checkHover(int mouseX, int mouseY) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        LabWork found = null;

        for (LabWork lab : labWorks) {
            int x = centerX + (int) lab.getCoordinates().getX();
            int y = centerY - (int) lab.getCoordinates().getY();
            int size = (int) Math.max(30, Math.min(100, lab.getMinimalPoint()));

            if (Point.distance(mouseX, mouseY, x, y) < size / 2.0) {
                found = lab;
                break;
            }
        }

        if (hoveredLab != found) {
            hoveredLab = found;
            if (found == null) hoverScale = 1.0f; // Сброс размера
            repaint();
        }
    }
}
