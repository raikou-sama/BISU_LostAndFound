package lostandfound.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class UITheme {

    // ── Color Palette ─────────────────────────────────────────────────────────
    public static final Color PRIMARY        = new Color(26, 58, 107);
    public static final Color PRIMARY_DARK   = new Color(14, 35, 70);
    public static final Color PRIMARY_LIGHT  = new Color(40, 90, 160);
    public static final Color ACCENT         = new Color(255, 193, 7);
    public static final Color SUCCESS        = new Color(25, 135, 84);
    public static final Color DANGER         = new Color(176, 42, 42);
    public static final Color WARNING        = new Color(180, 110, 0);
    public static final Color BG_LIGHT       = new Color(244, 246, 251);
    public static final Color TEXT_DARK      = new Color(22, 27, 50);
    public static final Color TEXT_GRAY      = new Color(95, 105, 135);
    public static final Color BORDER_COLOR   = new Color(208, 213, 228);
    public static final Color SIDEBAR_ADMIN  = new Color(14, 35, 70);
    public static final Color SIDEBAR_BG  = new Color(14, 35, 70);
    public static final Color SIDEBAR_STUDENT= new Color(10, 55, 30);
    public static final Color LOST_COLOR     = new Color(176, 42, 42);
    public static final Color FOUND_COLOR    = new Color(25, 135, 84);
    public static final Color CLAIMED_COLOR  = new Color(70, 115, 195);
    public static final Color PENDING_COLOR  = new Color(180, 110, 0);
    public static final Color WELCOME_HERO   = new Color(26, 58, 107);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_HERO    = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_NAV     = new Font("Segoe UI", Font.BOLD, 12);

    // ── Button Factories ──────────────────────────────────────────────────────
    public static JButton createPrimaryButton(String text) { return btn(text, PRIMARY, Color.WHITE); }
    public static JButton createSuccessButton(String text) { return btn(text, SUCCESS, Color.WHITE); }
    public static JButton createDangerButton(String text)  { return btn(text, DANGER,  Color.WHITE); }
    public static JButton createWarningButton(String text) { return btn(text, WARNING, Color.WHITE); }
    public static JButton createAccentButton(String text)  { return btn(text, ACCENT,  TEXT_DARK);   }

    private static JButton btn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(fg);
        b.setFont(FONT_BUTTON); b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        return b;
    }

    // ── Input Fields ──────────────────────────────────────────────────────────
    public static JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)));
        return tf;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)));
        return pf;
    }

    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_BODY); cb.setBackground(Color.WHITE);
        return cb;
    }

    public static JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL); l.setForeground(TEXT_DARK);
        return l;
    }

    // ── Table Styling ─────────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(210, 225, 255));
        table.setSelectionForeground(TEXT_DARK);
        table.setFillsViewportHeight(true);
        JTableHeader h = table.getTableHeader();
        h.setFont(FONT_LABEL);
        h.setBackground(PRIMARY);
        h.setForeground(Color.WHITE);
        h.setBorder(BorderFactory.createEmptyBorder());
        ((DefaultTableCellRenderer) h.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
    }

    public static void applyGlobalTheme() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
    }
}