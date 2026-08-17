package sms;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Draws a simple bar chart (no external library needed) showing
 * how many students are enrolled in each course.
 */
public class ChartPanel extends JPanel {

    private final Map<String, Integer> counts;

    public ChartPanel(List<Student> students) {
        counts = new TreeMap<>();
        for (Student s : students) {
            String course = (s.getCourse() == null || s.getCourse().isBlank()) ? "Unspecified" : s.getCourse();
            counts.merge(course, 1, Integer::sum);
        }
        setBackground(Theme.card());
        setPreferredSize(new Dimension(500, 350));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        int chartBottom = height - padding;
        int chartTop = padding;
        int chartLeft = padding;
        int chartRight = width - 30;

        g2.setColor(Theme.text());
        g2.setFont(Theme.FONT_BOLD);
        g2.drawString("Students per Course", chartLeft, 30);

        if (counts.isEmpty()) {
            g2.setColor(Theme.subtext());
            g2.setFont(Theme.FONT_LABEL);
            g2.drawString("No data to display yet.", chartLeft, chartTop + 30);
            return;
        }

        int maxCount = counts.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        int barCount = counts.size();
        int chartWidth = chartRight - chartLeft;
        int barSlot = chartWidth / barCount;
        int barWidth = Math.min(60, barSlot - 20);

        // Axis line
        g2.setColor(Theme.border());
        g2.drawLine(chartLeft, chartBottom, chartRight, chartBottom);

        int i = 0;
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            int barHeight = (int) (((double) entry.getValue() / maxCount) * (chartBottom - chartTop - 20));
            int x = chartLeft + i * barSlot + (barSlot - barWidth) / 2;
            int y = chartBottom - barHeight;

            g2.setColor(Theme.PRIMARY);
            g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);

            g2.setColor(Theme.text());
            g2.setFont(Theme.FONT_BOLD);
            String countText = String.valueOf(entry.getValue());
            int textWidth = g2.getFontMetrics().stringWidth(countText);
            g2.drawString(countText, x + (barWidth - textWidth) / 2, y - 8);

            g2.setColor(Theme.subtext());
            g2.setFont(Theme.FONT_LABEL);
            String label = entry.getKey();
            int labelWidth = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, x + (barWidth - labelWidth) / 2, chartBottom + 20);

            i++;
        }
    }
}
