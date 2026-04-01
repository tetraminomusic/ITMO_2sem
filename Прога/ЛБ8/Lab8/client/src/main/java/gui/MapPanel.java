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

/**
 * Панель визуализации коллекции.
 * Реализована с использованием трансформации координат для предотвращения jitter-эффекта (дрожания).
 */
public class MapPanel extends JPanel {
    //копия данных для отрисовки
    private List<LabWork> labWorks = new ArrayList<>();
    private final Map<String, Color> userColors = new ConcurrentHashMap<>();
    private final MainFrame mainFrame;

    private LabWork hoveredLab = null; //на какой объект сейчас смотрит мышка
    private float hoverScale = 1.0f;
    private final Timer hoverTimer;

    // Параметры смещения
    private double offsetX = 0;
    private double offsetY = 0;
    private double camX = 0;
    private double camY = 0;
    private Point lastMousePoint;

    // Параметры зума
    private double zoom = 1.0;
    private static final double MIN_ZOOM = 0.2;
    private static final double MAX_ZOOM = 10.0;
    private static final double ZOOM_STEP = 0.1;

    private final Font comicFont = new Font("Comic Sans MS", Font.BOLD, 12);

    public MapPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(Color.WHITE);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePoint = e.getPoint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleSelection(e.getX(), e.getY());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // Плавный расчет офсета для параллакса
                offsetX = (e.getX() - getWidth() / 2.0) / 35.0;
                offsetY = (e.getY() - getHeight() / 2.0) / 35.0;
                checkHover(e.getX(), e.getY());
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastMousePoint != null) {
                    // При перетаскивании зум не должен влиять на скорость перемещения
                    camX += (e.getX() - lastMousePoint.x);
                    camY += (e.getY() - lastMousePoint.y);
                    lastMousePoint = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) zoom = Math.min(MAX_ZOOM, zoom + ZOOM_STEP);
                else zoom = Math.max(MIN_ZOOM, zoom - ZOOM_STEP);
                repaint();
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);

        //пульсация объекта
        hoverTimer = new Timer(30, e -> {
            if (hoveredLab != null) {
                hoverScale = (float) (1.1 + 0.05 * Math.sin(System.currentTimeMillis() / 150.0));
                repaint();
            }
        });
        hoverTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //очистка экрана
        Graphics2D g2 = (Graphics2D) g;

        //сглаживание
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setFont(comicFont);

        //вычисление логического центра окна
        double totalOriginX = getWidth() / 2.0 + camX + offsetX;
        double totalOriginY = getHeight() / 2.0 + camY + offsetY;


        //рисуем сетку под объектами
        drawStableGrid(g2, totalOriginX, totalOriginY);
        g2.setStroke(new BasicStroke(2));
        g2.setColor(new Color(200, 200, 200));
        g2.draw(new java.awt.geom.Line2D.Double(totalOriginX, 0, totalOriginX, getHeight()));
        g2.draw(new java.awt.geom.Line2D.Double(0, totalOriginY, getWidth(), totalOriginY));
        g2.setStroke(new BasicStroke(1));

        //сами объекты
        for (LabWork lab : labWorks) {
            Color color = getUserColor(lab.getOwnerLogin());

            //положение
            double x = totalOriginX + (lab.getCoordinates().getX() * zoom);
            double y = totalOriginY - (lab.getCoordinates().getY() * zoom); // Инверсия Y

            //размер
            double baseSize = Math.max(30, Math.min(70, lab.getMinimalPoint()));
            double currentSize = (lab == hoveredLab) ? baseSize * hoverScale : baseSize;

            if (lab == hoveredLab) {
                //тень
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fill(new java.awt.geom.Ellipse2D.Double(x - currentSize / 2.0 + 4, y - currentSize / 2.0 + 4, currentSize, currentSize));
            }

            g2.setColor(color);
            g2.fill(new java.awt.geom.Ellipse2D.Double(x - currentSize / 2.0, y - currentSize / 2.0, currentSize, currentSize));
            g2.setColor(Color.BLACK);
            g2.draw(new java.awt.geom.Ellipse2D.Double(x - currentSize / 2.0, y - currentSize / 2.0, currentSize, currentSize));

            if (zoom > 0.4) {
                g2.drawString("ID:" + lab.getId(), (float) (x - 15), (float) (y - currentSize / 2.0 - 5));
            }
        }
    }

    /**
     * Рисует сетку, математически привязанную к логическому центру (originX, originY).
     */
    private void drawStableGrid(Graphics2D g2, double originX, double originY) {
        double gridSize = 50.0 * zoom;
        if (gridSize < 10) return;

        g2.setColor(new Color(240, 240, 240));
        g2.setStroke(new BasicStroke(1));

        for (double x = originX; x <= getWidth(); x += gridSize) {
            g2.draw(new java.awt.geom.Line2D.Double(x, 0, x, getHeight()));
        }

        for (double x = originX - gridSize; x >= 0; x -= gridSize) {
            g2.draw(new java.awt.geom.Line2D.Double(x, 0, x, getHeight()));
        }

        for (double y = originY; y <= getHeight(); y += gridSize) {
            g2.draw(new java.awt.geom.Line2D.Double(0, y, getWidth(), y));
        }

        for (double y = originY - gridSize; y >= 0; y -= gridSize) {
            g2.draw(new java.awt.geom.Line2D.Double(0, y, getWidth(), y));
        }
    }

    /**
     * Метод, который определяет, какой на какой объект сейчас наведена мышка
     * Просто считает по координатам и всё, волшебства так такого нет
     */
    private void checkHover(int mouseX, int mouseY) {
        double originX = getWidth() / 2.0 + camX + offsetX;
        double originY = getHeight() / 2.0 + camY + offsetY;
        LabWork found = null;

        for (LabWork lab : labWorks) {
            double x = originX + (lab.getCoordinates().getX() * zoom);
            double y = originY - (lab.getCoordinates().getY() * zoom);
            int size = (int) Math.max(30, Math.min(70, lab.getMinimalPoint()));

            if (Point.distance(mouseX, mouseY, x, y) < size / 2.0) {
                found = lab;
                break;
            }
        }
        if (hoveredLab != found) {
            hoveredLab = found;
            if (found == null) hoverScale = 1.0f;
            repaint();
        }
    }

    private void handleSelection(int mouseX, int mouseY) {
        double originX = getWidth() / 2.0 + camX + offsetX;
        double originY = getHeight() / 2.0 + camY + offsetY;
        LabWork found = null;

        for (int i = labWorks.size() - 1; i >= 0; i--) {
            LabWork lab = labWorks.get(i);
            double x = originX + (lab.getCoordinates().getX() * zoom);
            double y = originY - (lab.getCoordinates().getY() * zoom);
            int size = (int) Math.max(30, Math.min(70, lab.getMinimalPoint()));

            if (Point.distance(mouseX, mouseY, x, y) < size / 2.0) {
                found = lab;
                break;
            }
        }
        if (found != null) showInfoDialog(found);
    }

    /**
     * Метод, который создаёт окно с информацией о лабе
     * По сути один огромный HTML
     */
    private void showInfoDialog(LabWork found) {
        ResourceManager i18n = ResourceManager.getInstance();
        Color userColor = getUserColor(found.getOwnerLogin());

        //превращаем цвет в 16-ричку, чтобы юзануть для HTML
        String hexColor = String.format("#%02x%02x%02x", userColor.getRed(), userColor.getGreen(), userColor.getBlue());

        StringBuilder sb = new StringBuilder();
        sb.append("<html><div style='width: 380px; padding: 10px; font-family: Comic Sans MS;'>");
        sb.append("<h1 style='margin:0; color:").append(hexColor).append("; font-size: 24px;'>").append(found.getName()).append("</h1>");
        sb.append("<div style='color: #95a5a6; font-size: 11px; margin-bottom: 5px;'>ID: ").append(found.getId()).append("</div>");
        sb.append("<div style='height: 4px; background: ").append(hexColor).append("; margin-bottom: 15px;'></div>");
        sb.append("<table border='0' cellspacing='0' cellpadding='4' style='width:100%;'>");
        appendNiceRow(sb, i18n.getString("table.col.owner"), found.getOwnerLogin());
        appendNiceRow(sb, i18n.getString("table.col.point"), i18n.formatNumber(found.getMinimalPoint()));
        appendNiceRow(sb, i18n.getString("table.col.diff"), found.getDifficulty().toString());
        appendNiceRow(sb, i18n.getString("table.col.date"), i18n.formatDate(found.getCreationDate()));
        appendNiceRow(sb, "Координаты", "X: " + found.getCoordinates().getX() + ", Y: " + found.getCoordinates().getY());
        sb.append("</table>");
        if (found.getAuthor() != null) {
            models.Person a = found.getAuthor();
            sb.append("<div style='margin-top: 20px; padding: 12px; border: 1px dashed #cccccc; border-radius: 8px;'>");
            sb.append("<b style='color:").append(hexColor).append("; font-size: 16px;'>")
                    .append(i18n.getString("table.col.author")).append("</b><br>");

            sb.append("<div style='margin-top: 5px; color: #34495e;'>");
            sb.append("<b>•</b> ").append(a.getName()).append("<br>");

            if (a.getBirthday() != null) {
                String bday = a.getBirthday().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                sb.append("<b>•</b> ").append(i18n.getString("form.label.author.birthday")).append(" ").append(bday).append("<br>");
            }

            sb.append("<b>•</b> ").append(i18n.getString("form.label.author.height")).append(" ").append(a.getHeight()).append("<br>");

            if (a.getWeight() != null) {
                sb.append("<b>•</b> ").append(i18n.getString("form.label.author.weight")).append(" ").append(a.getWeight()).append("<br>");
            }
            if (a.getPassportID() != null && !a.getPassportID().isEmpty()) {
                sb.append("<b>•</b> ").append(i18n.getString("form.label.author.passport")).append(" <span style='font-family: monospace;'>").append(a.getPassportID()).append("</span>");
            }
            sb.append("</div></div>");
        }
        sb.append("</div></html>");

        JOptionPane.showMessageDialog(this, sb.toString(), i18n.getString("info.title"), JOptionPane.PLAIN_MESSAGE);
    }

    private void appendNiceRow(StringBuilder sb, String label, String value) {
        sb.append("<tr><td style='color: #7f8c8d; font-size: 13px; width: 40%;'>").append(label).append("</td>")
                .append("<td style='color: #2c3e50; font-size: 13px;'><b>").append(value).append("</b></td></tr>");
    }

    /**
     * Метод, который вычисляет цвет конкретного пользователя
     */
    private Color getUserColor(String login) {
        if (login == null) return Color.GRAY;
        return userColors.computeIfAbsent(login, k -> {
            Random r = new Random(k.hashCode());
            return new Color(r.nextInt(200), r.nextInt(200), r.nextInt(200));
        });
    }

    public void updateData(List<LabWork> newData) {
        this.labWorks = new ArrayList<>(newData);
        repaint();
    }
}