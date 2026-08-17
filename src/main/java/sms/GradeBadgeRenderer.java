package sms;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Renders the Grade column as a small colored rounded pill instead of plain text.
 */
public class GradeBadgeRenderer extends DefaultTableCellRenderer {

    private String grade = "-";

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int row, int column) {
        grade = value == null ? "-" : value.toString();
        setText(grade);
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(Theme.FONT_BOLD);
        setForeground(Color.WHITE);
        setOpaque(false); // we paint our own rounded background
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Store the row's normal background so gaps around the pill still look right.
        putClientProperty("rowBg", isSelected ? table.getSelectionBackground() : table.getBackground());
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill the whole cell with the row's background first.
        Color rowBg = (Color) getClientProperty("rowBg");
        if (rowBg != null) {
            g2.setColor(rowBg);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // Draw the centered pill.
        FontMetrics fm = g2.getFontMetrics(Theme.FONT_BOLD);
        int textWidth = fm.stringWidth(grade);
        int pillWidth = textWidth + 24;
        int pillHeight = 24;
        int x = (getWidth() - pillWidth) / 2;
        int y = (getHeight() - pillHeight) / 2;

        g2.setColor(GradeUtil.colorFor(grade));
        g2.fillRoundRect(x, y, pillWidth, pillHeight, 14, 14);
        g2.dispose();

        super.paintComponent(g);
    }
}
