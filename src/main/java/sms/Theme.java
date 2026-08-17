package sms;

import java.awt.Color;
import java.awt.Font;

/**
 * Central place for all colors/fonts so the whole app keeps
 * one consistent light theme. Change a value here and it updates everywhere.
 */
public class Theme {

    private static final Color BACKGROUND = new Color(0xF5, 0xF7, 0xFA);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(0x22, 0x27, 0x33);
    private static final Color SUBTEXT = new Color(0x6B, 0x72, 0x80);
    private static final Color BORDER = new Color(0xE1, 0xE5, 0xEA);

    public static final Color PRIMARY = new Color(0x4A, 0x6C, 0xF2);
    public static final Color PRIMARY_DARK = new Color(0x37, 0x53, 0xC9);
    public static final Color DANGER = new Color(0xE0, 0x4F, 0x4F);
    public static final Color SUCCESS = new Color(0x2E, 0xA5, 0x6A);
    public static final Color WARNING = new Color(0xE0, 0x9A, 0x2E);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    public static Color background() {
        return BACKGROUND;
    }

    public static Color card() {
        return CARD;
    }

    public static Color text() {
        return TEXT;
    }

    public static Color subtext() {
        return SUBTEXT;
    }

    public static Color border() {
        return BORDER;
    }
}
