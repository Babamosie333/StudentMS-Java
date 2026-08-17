package sms;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Renders the Name column with a small colored circle avatar (initials)
 * before the name, similar to Gmail/contact-list style avatars.
 */
public class AvatarNameRenderer extends DefaultTableCellRenderer {

    private static final Color[] PALETTE = {
            new Color(0x4A, 0x6C, 0xF2), new Color(0x2E, 0xA5, 0x6A),
            new Color(0xE0, 0x9A, 0x2E), new Color(0xE0, 0x4F, 0x4F),
            new Color(0x8E, 0x5A, 0xE0), new Color(0x2E, 0xC2, 0xC2)
    };

    private String name = "";

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int row, int column) {
        name = value == null ? "" : value.toString();
        setText("  " + name);
        setFont(Theme.FONT_LABEL);
        setForeground(Theme.text());
        setOpaque(true);
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        setBorder(BorderFactory.createEmptyBorder(0, 38, 0, 0)); // leave room for the circle
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (name.isBlank()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int diameter = 26;
        int x = 6;
        int y = (getHeight() - diameter) / 2;

        Color avatarColor = PALETTE[Math.abs(name.hashCode()) % PALETTE.length];
        g2.setColor(avatarColor);
        g2.fillOval(x, y, diameter, diameter);

        String initials = initialsFor(name);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (diameter - fm.stringWidth(initials)) / 2;
        int textY = y + (diameter + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(initials, textX, textY);

        g2.dispose();
    }

    private String initialsFor(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) return "?";
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(parts[0].charAt(0)));
        if (parts.length > 1 && !parts[parts.length - 1].isEmpty()) {
            sb.append(Character.toUpperCase(parts[parts.length - 1].charAt(0)));
        }
        return sb.toString();
    }
}
