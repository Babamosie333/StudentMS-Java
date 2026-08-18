package sms;

import javax.swing.*;
import java.awt.*;
import java.awt.Desktop;
import java.net.URI;

/**
 * A small round icon button drawn with plain Java2D (a stylized "GH" mark,
 * so no external image file is needed). Clicking it opens the given URL
 * in the user's default browser.
 */
public class LinkIconButton extends JButton {

    private final String glyph;
    private boolean hovering = false;

    public LinkIconButton(String glyph, String url, String tooltip) {
        this.glyph = glyph;
        setToolTipText(tooltip);
        setPreferredSize(new Dimension(36, 36));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                hovering = true;
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hovering = false;
                repaint();
            }
        });

        addActionListener(e -> openLink(url));
    }

    private void openLink(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Couldn't open the link automatically.\nVisit: " + url,
                    "Open Link", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight());
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        g2.setColor(hovering ? new Color(0x18, 0x23, 0x31) : new Color(0x2E, 0x35, 0x40));
        g2.fillOval(x, y, size, size);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (size - fm.stringWidth(glyph)) / 2;
        int textY = y + (size + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(glyph, textX, textY);

        g2.dispose();
    }
}
