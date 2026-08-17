package sms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * First screen the user sees. Asks for the MongoDB Atlas
 * connection string and the database name, then connects.
 */
public class ConnectionView extends JFrame {

    private final JTextField uriField;
    private final JTextField dbNameField;
    private final JLabel statusLabel;
    private final DBHandler dbHandler;

    public ConnectionView() {
        dbHandler = new DBHandler();

        setTitle("Student Management System - Connect to Database");
        setSize(520, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.background());
        setLayout(new BorderLayout());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.card());
        card.setBorder(new EmptyBorder(35, 40, 35, 40));

        JLabel title = new JLabel("Connect to MongoDB Atlas");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.text());
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter your Atlas connection string to continue");
        subtitle.setFont(Theme.FONT_LABEL);
        subtitle.setForeground(Theme.subtext());
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(4, 0, 25, 0));

        JLabel uriLabel = makeFieldLabel("Connection String (URI)");
        uriField = new JTextField("mongodb+srv://sudhanshu:sudhanshu@minorproject.ya4tkqy.mongodb.net/?appName=minorproject");
        styleField(uriField);

        JLabel dbLabel = makeFieldLabel("Database Name");
        dbNameField = new JTextField("student_management");
        styleField(dbNameField);

        RoundedButton connectBtn = new RoundedButton("Connect", Theme.PRIMARY, Theme.PRIMARY_DARK);
        connectBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        connectBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        connectBtn.setPreferredSize(new Dimension(200, 42));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.FONT_LABEL);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setBorder(new EmptyBorder(12, 0, 0, 0));

        connectBtn.addActionListener(e -> onConnect());

        card.add(title);
        card.add(subtitle);
        card.add(uriLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(uriField);
        card.add(Box.createVerticalStrut(16));
        card.add(dbLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(dbNameField);
        card.add(Box.createVerticalStrut(22));
        card.add(connectBtn);
        card.add(statusLabel);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Theme.background());
        wrapper.add(card);

        add(wrapper, BorderLayout.CENTER);
    }

    private JLabel makeFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_BOLD);
        l.setForeground(Theme.text());
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleField(JTextField field) {
        field.setFont(Theme.FONT_LABEL);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.border(), 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void onConnect() {
        String uri = uriField.getText().trim();
        String dbName = dbNameField.getText().trim();

        if (uri.isEmpty() || dbName.isEmpty()) {
            statusLabel.setForeground(Theme.DANGER);
            statusLabel.setText("Please fill in both fields.");
            return;
        }

        statusLabel.setForeground(Theme.subtext());
        statusLabel.setText("Connecting...");

        // Run the connection attempt off the UI thread so the window doesn't freeze.
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            String error = null;

            @Override
            protected Void doInBackground() {
                try {
                    dbHandler.connect(uri, dbName);
                } catch (Exception ex) {
                    error = ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (error == null) {
                    statusLabel.setForeground(Theme.SUCCESS);
                    statusLabel.setText("Connected successfully!");
                    SwingUtilities.invokeLater(() -> {
                        new ManagementView(dbHandler).setVisible(true);
                        dispose();
                    });
                } else {
                    statusLabel.setForeground(Theme.DANGER);
                    statusLabel.setText("Connection failed: " + error);
                }
            }
        };
        worker.execute();
    }
}
