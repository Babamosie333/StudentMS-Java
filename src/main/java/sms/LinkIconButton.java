package sms;

import javax.swing.*;
import java.awt.*;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;

/**
 * A small round icon button. If an image icon is found on the classpath
 * (src/main/resources/icons/github.png) it draws that; otherwise it falls
 * back to a plain Java2D-drawn "GH" badge, so the app never crashes even
 * if the image file is missing. Clicking it opens the given URL in the
 * user's default browser.
 */
public class LinkIconButton extends JButton {

    private final String glyph;
    private final Image iconImage; // null if no image was found
    private boolean hovering = false;

    public LinkIconButton(String glyph, String url, String tooltip) {
        this(glyph, url, tooltip, null);
    }

    /**
     * @param resourcePath classpath path to an icon image, e.g. "/icons/github.png".
     *                     Pass null to always use the drawn "GH" badge.
     */
    public LinkIconButton(String glyph, String url, String tooltip, String resourcePath) {
        this.glyph = glyph;
        this.iconImage = loadImage(resourcePath);

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

    /** Loads an image from the classpath. Returns null (safely) if not found. */
    private Image loadImage(String resourcePath) {
        if (resourcePath == null) return null;
        try {
            URL url = getClass().getResource(resourcePath);
            if (url == null) return null; // file missing - fall back to drawn badge
            return new ImageIcon(url).getImage();
        } catch (Exception e) {
            return null;
        }
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
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int size = Math.min(getWidth(), getHeight());
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        g2.setColor(hovering ? new Color(0x18, 0x23, 0x31) : new Color(0x2E, 0x35, 0x40));
        g2.fillOval(x, y, size, size);

        if (iconImage != null) {
            // Draw the real icon image, scaled down to fit inside the circle with a small margin.
            int pad = 8;
            g2.drawImage(iconImage, x + pad, y + pad, size - pad * 2, size - pad * 2, this);
        } else {
            // Fallback: draw the "GH" text badge.
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            int textX = x + (size - fm.stringWidth(glyph)) / 2;
            int textY = y + (size + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(glyph, textX, textY);
        }

        g2.dispose();
    }
}