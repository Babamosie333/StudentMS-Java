package sms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Small auto-disappearing notification banner (like a mobile app "toast"),
 * used instead of blocking JOptionPane popups for simple success messages.
 */
public class Toast {

    public static void show(JFrame owner, String message, Color accent) {
        JWindow window = new JWindow(owner);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x22, 0x27, 0x33));
        panel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(Theme.FONT_BOLD);

        JPanel stripe = new JPanel();
        stripe.setBackground(accent);
        stripe.setPreferredSize(new Dimension(4, 10));

        panel.add(stripe, BorderLayout.WEST);
        panel.add(label, BorderLayout.CENTER);

        window.getContentPane().add(panel);
        window.pack();

        // Position bottom-right of the owner window
        Point ownerLoc = owner.getLocationOnScreen();
        int x = ownerLoc.x + owner.getWidth() - window.getWidth() - 30;
        int y = ownerLoc.y + owner.getHeight() - window.getHeight() - 30;
        window.setLocation(x, y);
        window.setOpacity(1f);
        window.setVisible(true);

        Timer timer = new Timer(1600, e -> window.dispose());
        timer.setRepeats(false);
        timer.start();
    }

    public static void success(JFrame owner, String message) {
        show(owner, message, Theme.SUCCESS);
    }

    public static void error(JFrame owner, String message) {
        show(owner, message, Theme.DANGER);
    }
}
