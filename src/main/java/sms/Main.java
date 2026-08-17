package sms;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point. Just shows the connection screen first.
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new ConnectionView().setVisible(true));
    }
}
