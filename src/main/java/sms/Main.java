package sms;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Entry point. Just shows the connection screen first.
 * Also installs a safety net so that if anything crashes, the user sees
 * an error message (and a log file) instead of the app silently closing.
 */
public class Main {
    public static void main(String[] args) {
        // Catch ANY unexpected crash (on any thread) so the app never just
        // "disappears" with no explanation - this writes a log file next to
        // the app and shows a popup with the error.
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            String message = writeCrashLog(ex);
            JOptionPane.showMessageDialog(null,
                    "Something went wrong and the app needs to close.\n\n" + message,
                    "Unexpected Error", JOptionPane.ERROR_MESSAGE);
        });

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new ConnectionView().setVisible(true));
    }

    private static String writeCrashLog(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String fullTrace = sw.toString();

        try {
            File logFile = new File(System.getProperty("user.home"), "StudentMS-error-log.txt");
            try (PrintWriter writer = new PrintWriter(logFile)) {
                writer.println(fullTrace);
            }
            return ex.getMessage() + "\n\nFull details saved to:\n" + logFile.getAbsolutePath();
        } catch (Exception loggingFailed) {
            return fullTrace;
        }
    }
}