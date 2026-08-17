package sms;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * A simple flat, rounded-corner button used everywhere in the app
 * so buttons look modern instead of the default grey Swing button.
 */
public class RoundedButton extends JButton {

    private final Color baseColor;
    private final Color hoverColor;

    public RoundedButton(String text, Color baseColor, Color hoverColor) {
        super(text);
        this.baseColor = baseColor;
        this.hoverColor = hoverColor;
        setFont(Theme.FONT_BUTTON);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getModel().isRollover() ? hoverColor : baseColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
}
